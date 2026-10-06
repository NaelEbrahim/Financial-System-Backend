package com.example.novabank.identity.Event.produce;

public record UserCreatedEvent(
        Long userId,
        String username,
        String email,
        String firstname,
        String lastname
) {}