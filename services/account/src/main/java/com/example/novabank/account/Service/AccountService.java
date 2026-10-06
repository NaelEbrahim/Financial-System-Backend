package com.example.novabank.account.Service;

import com.example.novabank.account.Client.IdentityClient;
import com.example.novabank.account.DTO.Response.AccountInfoResponse;
import com.example.novabank.account.DTO.Response.FindAccountResponse;
import com.example.novabank.account.Enum.OutBoxEventType;
import com.example.novabank.account.Event.consume.TransferRequestedEvent;
import com.example.novabank.account.Event.produce.TransferFinishedEvent;
import com.example.novabank.account.Event.produce.AccountCreatedEvent;
import com.example.novabank.account.Event.consume.UserCreatedEvent;
import com.example.novabank.account.Exception.CustomException;
import com.example.novabank.account.Model.AccountModel;
import com.example.novabank.account.Repository.AccountRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final GeneratorService generatorService;

    private final OutboxService outboxService;

    private final ObjectMapper objectMapper;

    private final IdentityClient identityClient;


    @Transactional
    @KafkaListener(topics = "USER_CREATED", groupId = "account-group")
    public void CreateAccount(String payload) throws JsonProcessingException {
        UserCreatedEvent event =
                objectMapper.readValue(
                        payload,
                        UserCreatedEvent.class
                );

        if (accountRepository.existsByUserId(event.userId())) {
            throw new CustomException("user already has account", HttpStatus.CONFLICT);
        }

        AccountModel newAccount = new AccountModel();
        newAccount.setUserId(event.userId());
        newAccount.setWalletKey(generatorService.generateWalletKey());
        accountRepository.save(newAccount);

        var newEvent = new AccountCreatedEvent(
                event.userId(),
                event.username(),
                event.email(),
                event.firstname(),
                event.lastname()
        );

        outboxService.saveEvent(OutBoxEventType.ACCOUNT_CREATED, newEvent);
    }

    public AccountInfoResponse getAccountInfo() {
        var userId = getUserIdFromContextHolder();
        var userAccount = accountRepository.findByUserId(userId).
                orElseThrow(() -> new CustomException(
                        "problem with your account",
                        HttpStatus.BAD_REQUEST));
        return new AccountInfoResponse(userAccount.getWalletKey(), userAccount.getBalance());
    }

    private Long getUserIdFromContextHolder() {
        return Long.valueOf(String.valueOf(Objects
                .requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication()).getPrincipal()));
    }

    @Transactional
    @KafkaListener(topics = "TRANSFER_REQUESTED", groupId = "account-group")
    public void transferMoney(String payload) throws JsonProcessingException {
        TransferRequestedEvent event =
                objectMapper.readValue(
                        payload,
                        TransferRequestedEvent.class
                );

        var sender = accountRepository.findByUserId(event.senderId()).orElse(null);
        var receiver = accountRepository.findByUserId(event.receiverId()).orElse(null);

        var newEvent = new TransferFinishedEvent(
                event.senderId(),
                event.receiverId(),
                event.amount(),
                LocalDateTime.now(),
                event.reference_number()
        );

        if (sender != null && receiver != null) {
            if (identityClient.isUserIdCorrect(sender.getUserId()) != null && identityClient.isUserIdCorrect(receiver.getUserId()) != null) {
                if (sender.getBalance().compareTo(event.amount()) >= 0) {
                    sender.setBalance(sender.getBalance().subtract(event.amount()));
                    receiver.setBalance(receiver.getBalance().add(event.amount()));

                    outboxService.saveEvent(OutBoxEventType.TRANSFER_COMPLETED, newEvent);
                    return;
                }
            }
        }
        outboxService.saveEvent(OutBoxEventType.TRANSFER_FAILED, newEvent);
    }

    public FindAccountResponse findAccountByWalletKey(String walletKey) {
        var account = accountRepository.findByWalletKey(walletKey).orElse(null);
        if (account != null) {
            return new FindAccountResponse(account.getUserId(), account.getBalance());
        }
        return null;
    }

    public FindAccountResponse findAccountByUserId(Long userId) {
        var account = accountRepository.findByUserId(userId).orElse(null);
        if (account != null) {
            return new FindAccountResponse(account.getUserId(), account.getBalance());
        }
        return null;
    }

}
