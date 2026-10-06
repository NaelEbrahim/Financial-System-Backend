package com.example.novabank.identity.Event.produce;

import java.time.LocalDateTime;

public record PasswordChangedEvent(
        String user_email,
        String user_firstname,
        String user_lastname,
        LocalDateTime date_time
) {
}
