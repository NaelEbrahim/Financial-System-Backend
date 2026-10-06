package com.example.novabank.identity.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email
) {
}
