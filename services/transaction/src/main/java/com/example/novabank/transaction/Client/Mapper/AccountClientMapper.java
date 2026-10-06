package com.example.novabank.transaction.Client.Mapper;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AccountClientMapper {

    private Long userId;

    private BigDecimal balance;

}
