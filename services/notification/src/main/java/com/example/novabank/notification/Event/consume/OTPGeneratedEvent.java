package com.example.novabank.notification.Event.consume;

public record OTPGeneratedEvent(
        String user_email,
        String user_firstname,
        String user_lastname,
        String otpCode
) {
}
