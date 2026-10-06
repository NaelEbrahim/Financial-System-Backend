package com.example.novabank.notification.Event.consume;

public record UserCreationPendingEvent(
        String email,
        String firstname,
        String lastname,
        String otp
) {
}
