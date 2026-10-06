package com.example.novabank.transaction.Service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class GeneratorService {

    private static final String CHAR_POOL =
            "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final SecureRandom random = new SecureRandom();

    public String generateTransactionNumber() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyMMdd"));

        StringBuilder builder = new StringBuilder("NB");
        builder.append(date);

        for (int i = 0; i < 7; i++) {
            builder.append(
                    CHAR_POOL.charAt(
                            random.nextInt(CHAR_POOL.length())
                    )
            );
        }

        return builder.toString();
    }

}
