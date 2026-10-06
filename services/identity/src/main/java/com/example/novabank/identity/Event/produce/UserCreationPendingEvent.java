package com.example.novabank.identity.Event.produce;

public record UserCreationPendingEvent(
        String email,
        String firstname,
        String lastname,
        String otp
) {
}
