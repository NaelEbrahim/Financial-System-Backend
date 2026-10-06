package com.example.novabank.identity.Event.produce;

public record RegistrationRequestedEvent(
        String email,
        String firstname,
        String lastname,
        String pin,
        String password
) {}