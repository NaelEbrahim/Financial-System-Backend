package com.example.novabank.account.Model;

import com.example.novabank.account.Enum.OutBoxEventType;
import com.example.novabank.account.Enum.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity(name = "outbox")
public class OutBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private OutBoxEventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false)
    private OutboxStatus status = OutboxStatus.PENDING;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

}
