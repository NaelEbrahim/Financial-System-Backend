package com.example.novabank.transaction.DTO.Response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailyTotalsResponse(
        BigDecimal totalIncome,

        BigDecimal totalOutcome,

        @JsonFormat(pattern = "dd MMM yyyy")
        LocalDate todayDate
) {
}
