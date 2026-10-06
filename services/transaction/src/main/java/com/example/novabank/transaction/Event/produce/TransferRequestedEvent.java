package com.example.novabank.transaction.Event.produce;

import java.math.BigDecimal;

public record TransferRequestedEvent(
        Long senderId,
        Long receiverId,
        BigDecimal amount,
        String reference_number
) {
}
