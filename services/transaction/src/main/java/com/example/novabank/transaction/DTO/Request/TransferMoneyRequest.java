package com.example.novabank.transaction.DTO.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferMoneyRequest(
        @NotBlank(message = "receiver account required")
        String receiver_account,


        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.1", message = "Amount must be greater than 0")
        BigDecimal amount,

        @Size(min = 6, max = 6)
        @NotBlank(message = "receiver account required")
        String pin
) {
}
