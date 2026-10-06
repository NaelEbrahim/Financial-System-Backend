package com.example.novabank.identity.Event.produce;

public record OTPGeneratedEvent(
        String user_email,
        String user_firstname,
        String user_lastname,
        String otpCode
) {
}
