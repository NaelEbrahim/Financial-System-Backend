package com.example.novabank.identity.DTO.Request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "refresh token is required")
        String refreshtoken
) {
}
