package com.example.novabank.transaction.Service;

import com.example.novabank.transaction.Client.AccountClient;
import com.example.novabank.transaction.Client.IdentityClient;
import com.example.novabank.transaction.Config.SecurityConfig;
import com.example.novabank.transaction.DTO.Request.TransferMoneyRequest;
import com.example.novabank.transaction.DTO.Response.DailyTotalsResponse;
import com.example.novabank.transaction.DTO.Response.TransactionHistoryResponse;
import com.example.novabank.transaction.Enum.OutBoxEventType;
import com.example.novabank.transaction.Enum.Status;
import com.example.novabank.transaction.Event.consume.TransferFinishedEvent;
import com.example.novabank.transaction.Event.produce.TransferRequestedEvent;
import com.example.novabank.transaction.Exception.CustomException;
import com.example.novabank.transaction.Model.TransactionModel;
import com.example.novabank.transaction.Repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final OutboxService outboxService;

    private final IdentityClient identityClient;

    private final AccountClient accountClient;

    private final GeneratorService generatorService;

    private final ObjectMapper objectMapper;

    private final SecurityConfig securityConfig;


    @Transactional
    public void requestTransferMoney(TransferMoneyRequest transferMoneyRequest) throws JsonProcessingException {
        var senderId = getUserIdFromContextHolder();

        var senderAccount = accountClient.findAccountByUserId(senderId);
        var receiverAccount = accountClient.findAccountByWalletKey(transferMoneyRequest.receiver_account());

        var isSenderValid = senderAccount != null && identityClient.isUserIdCorrect(senderAccount.getUserId()) != null;
        var isReceiverValid = receiverAccount != null && identityClient.isUserIdCorrect(receiverAccount.getUserId()) != null;

        if (isSenderValid && isReceiverValid) {
            if (!senderAccount.getUserId().equals(receiverAccount.getUserId())) {
                if (securityConfig.passwordEncoder().matches(transferMoneyRequest.pin(), identityClient.isUserIdCorrect(senderId))) {
                    if (senderAccount.getBalance().compareTo(transferMoneyRequest.amount()) >= 0) {
                        var reference_number = generatorService.generateTransactionNumber();
                        TransactionModel newTransaction = new TransactionModel();
                        newTransaction.setSenderId(senderId);
                        newTransaction.setReceiverId(receiverAccount.getUserId());
                        newTransaction.setAmount(transferMoneyRequest.amount());
                        newTransaction.setReferenceNumber(reference_number);
                        newTransaction.setStatus(Status.PENDING);

                        transactionRepository.save(newTransaction);

                        var newEvent = new TransferRequestedEvent(
                                senderId,
                                receiverAccount.getUserId(),
                                transferMoneyRequest.amount(),
                                reference_number
                        );

                        outboxService.saveEvent(OutBoxEventType.TRANSFER_REQUESTED, newEvent);
                    } else {
                        throw new CustomException("insufficient balance", HttpStatus.BAD_REQUEST);
                    }
                } else {
                    throw new CustomException("invalid PIN", HttpStatus.BAD_REQUEST);
                }
            } else {
                throw new CustomException("you can not transfer to yourself", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new CustomException("problem with sender/receiver account", HttpStatus.BAD_REQUEST);
        }
    }

    private Long getUserIdFromContextHolder() {
        return Long.valueOf(String.valueOf(Objects
                .requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication()).getPrincipal()));
    }


    @Transactional
    @KafkaListener(topics = "TRANSFER_COMPLETED", groupId = "transaction-group")
    public void completeTransferState(String payload) throws JsonProcessingException {
        TransferFinishedEvent event =
                objectMapper.readValue(
                        payload,
                        TransferFinishedEvent.class
                );

        var transaction = transactionRepository.findByReferenceNumber(event.reference_number())
                .orElse(null);

        if (transaction != null) {
            transaction.setStatus(Status.COMPLETED);
            transaction.setUpdatedAt(LocalDateTime.now());
            return;
        }
        throw new CustomException("transaction not found", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    @KafkaListener(topics = "TRANSFER_FAILED", groupId = "transaction-group")
    public void failedTransferState(String payload) throws JsonProcessingException {
        TransferFinishedEvent event =
                objectMapper.readValue(
                        payload,
                        TransferFinishedEvent.class
                );

        var transaction = transactionRepository.findByReferenceNumber(event.reference_number())
                .orElse(null);

        if (transaction != null) {
            transaction.setStatus(Status.FAILED);
            transaction.setUpdatedAt(LocalDateTime.now());
            return;
        }
        throw new CustomException("transaction not found", HttpStatus.BAD_REQUEST);
    }

    public List<TransactionHistoryResponse> getUserTransactions(Pageable pageable) {
        var userId = getUserIdFromContextHolder();
        var userTransactions = transactionRepository.findByUserId(userId, pageable);

        var responseList = new ArrayList<TransactionHistoryResponse>();
        for (TransactionModel item : userTransactions) {
            responseList.add(new TransactionHistoryResponse(
                    item.getReferenceNumber(),
                    !item.getSenderId().equals(userId),
                    item.getAmount(),
                    item.getStatus(),
                    item.getCreatedAt()));
        }
        return responseList;
    }

    public DailyTotalsResponse getTodayTotals() {
        var userId = getUserIdFromContextHolder();

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        BigDecimal income = transactionRepository.sumIncome(userId, Status.COMPLETED, start, end);
        BigDecimal outcome = transactionRepository.sumOutcome(userId, Status.COMPLETED, start, end);

        return new DailyTotalsResponse(income, outcome,today);
    }

}
