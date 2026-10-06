package com.example.novabank.identity.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterVerifyOTPRequest(
        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "verifycode is required")
        @Size(min = 4, max = 4, message = "size must be 4 digit")
        String verifycode
) {
}
