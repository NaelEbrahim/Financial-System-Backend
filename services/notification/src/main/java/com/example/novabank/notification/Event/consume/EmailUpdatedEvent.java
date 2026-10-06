package com.example.novabank.notification.Event.consume;

import java.time.LocalDateTime;

public record EmailUpdatedEvent(
        Long user_id,
        String old_email,
        String new_email,
        String user_firstname,
        String user_lastname,
        LocalDateTime date_time
) {
}
