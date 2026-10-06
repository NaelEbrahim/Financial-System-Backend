package com.example.novabank.account.Event.produce;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferFinishedEvent(
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        LocalDateTime date,
        String reference_number
) {
}
