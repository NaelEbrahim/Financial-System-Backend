package com.example.novabank.account.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequest(
        @NotBlank(message = "user_id is required")
        Long userId,
        @NotBlank(message = "username is required")
        String username,
        @NotBlank(message = "email is required")
        String email,
        @NotBlank(message = "firstname is required")
        String firstname,
        @NotBlank(message = "lastname is required")
        String lastname
) {
}
