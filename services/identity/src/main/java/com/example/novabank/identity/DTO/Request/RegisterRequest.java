package com.example.novabank.identity.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record RegisterRequest(
        @NotBlank(message = "firstname is required")
        @Size(min = 3, message = "firstname too short")
        String firstname,

        @NotBlank(message = "lastname is required")
        @Size(min = 3, message = "lastname too short")
        String lastname,

        @NotBlank(message = "email is required")
        @Email(message = "invalid email format")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 6, message = "password must be at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$#@])[A-Za-z\\d$#@]+$",
                message = "Password must include upper case, lower case, number, and one symbol"
        )
        String password,

        @Pattern(
                regexp = "^(?!.*(\\d)\\1{5}$|123456$|654321$)\\d{6}$",
                message = "PIN must be 6 digits (cannot be repeated or sequential)"
        )
        String pin
) {
}
