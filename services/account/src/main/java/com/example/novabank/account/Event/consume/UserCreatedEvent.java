package com.example.novabank.account.Event.consume;

public record UserCreatedEvent(
        Long userId,
        String username,
        String email,
        String firstname,
        String lastname
) {}
