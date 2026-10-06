package com.example.novabank.notification.Event.consume;

public record AccountCreatedEvent(
        Long userId,
        String username,
        String email,
        String firstname,
        String lastname
) {
}
