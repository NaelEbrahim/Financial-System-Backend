package com.example.novabank.identity.DTO.Response;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record GetUserProfileResponse(
        String firstname,
        String lastname,
        String email,
        @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
        LocalDateTime updatedAt
) {
}
