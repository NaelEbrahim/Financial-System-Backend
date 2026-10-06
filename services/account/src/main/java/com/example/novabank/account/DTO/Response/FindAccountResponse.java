package com.example.novabank.account.DTO.Response;

import java.math.BigDecimal;

public record FindAccountResponse(
        Long userId,

        BigDecimal balance

) {
}
