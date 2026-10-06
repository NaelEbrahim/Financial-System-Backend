package com.example.novabank.account.DTO.Response;

import java.math.BigDecimal;

public record AccountInfoResponse(
        String accountnumber,

        BigDecimal accountbalance
) {
}
