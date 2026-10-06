package com.example.novabank.account.Service;

import com.example.novabank.account.Repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class GeneratorService {
    
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final int LENGTH = 12;

    private final AccountRepository accountRepository;


    public String generateWalletKey() {
        String key;
        do {
            key = generateRandomKey();
        } while (accountRepository.existsByWalletKey(key));

        return key;
    }

    private static String generateRandomKey() {
        StringBuilder key = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            key.append(RANDOM.nextInt(10));
        }

        return key.toString();
    }

}
