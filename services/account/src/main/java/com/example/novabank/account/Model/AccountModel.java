package com.example.novabank.account.Model;

import com.example.novabank.account.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity(name = "account")
public class AccountModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(unique = true, nullable = false)
    private String walletKey;

    @Column(columnDefinition = "timestamp(0)")
    private LocalDateTime updatedAt = LocalDateTime.now().withNano(0);

}
