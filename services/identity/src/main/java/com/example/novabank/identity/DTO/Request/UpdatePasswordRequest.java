package com.example.novabank.identity.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
        @NotBlank(message = "password is required")
        @Size(min = 6, message = "password must be at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$#@])[A-Za-z\\d$#@]+$",
                message = "Password must include upper case, lower case, number, and one symbol"
        )
        String oldpassword,

        @NotBlank(message = "password is required")
        @Size(min = 6, message = "password must be at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$#@])[A-Za-z\\d$#@]+$",
                message = "Password must include upper case, lower case, number, and one symbol"
        )
        String newpassword,

        @NotBlank(message = "confirmpassword is required")
        @Size(min = 6, message = "password must be at least 6 characters")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$#@])[A-Za-z\\d$#@]+$",
                message = "Password must include upper case, lower case, number, and one symbol"
        )
        String confirmpassword
) {
}
