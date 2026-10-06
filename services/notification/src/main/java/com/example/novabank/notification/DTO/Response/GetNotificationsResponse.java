package com.example.novabank.notification.DTO.Response;

import com.example.novabank.notification.Enum.Type;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record GetNotificationsResponse(
        Long id,
        String title,
        String content,
        Type type,
        boolean isRead,
        @JsonFormat(pattern = "dd MMM yyyy HH:mm:ss")
        LocalDateTime createdAt
) {
}