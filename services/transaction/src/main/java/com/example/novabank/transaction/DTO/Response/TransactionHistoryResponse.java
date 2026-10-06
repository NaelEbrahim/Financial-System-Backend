package com.example.novabank.transaction.DTO.Response;

import com.example.novabank.transaction.Enum.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionHistoryResponse(
        String referenceNumber,

        Boolean income,

        BigDecimal amount,

        Status status,

        @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
        LocalDateTime recordedAt
) {
}
