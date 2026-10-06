package com.example.novabank.account.Event.produce;

public record AccountCreatedEvent(
        Long userId,
        String username,
        String email,
        String firstname,
        String lastname
) {
}
