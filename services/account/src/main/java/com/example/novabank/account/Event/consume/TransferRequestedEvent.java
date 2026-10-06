package com.example.novabank.account.Event.consume;

import java.math.BigDecimal;

public record TransferRequestedEvent(
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        String reference_number
) {
}
