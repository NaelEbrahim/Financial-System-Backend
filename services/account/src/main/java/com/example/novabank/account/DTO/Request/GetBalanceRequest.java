package com.example.novabank.account.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GetBalanceRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "pin is required")
        @Size(min = 6, max = 6, message = "pin must be 6 digit")
        String pin
) {
}
