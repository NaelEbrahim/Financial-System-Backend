package com.example.novabank.transaction.Client;

import com.example.novabank.transaction.Client.Mapper.AccountClientMapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "account-service",
        url = "${application.config.account-url}"
)
public interface AccountClient {

    @GetMapping("/find-account-by-wallet-key/{walletKey}")
    AccountClientMapper findAccountByWalletKey(@PathVariable String walletKey);

    @GetMapping("/find-account-by-user-id/{userId}")
    AccountClientMapper findAccountByUserId(@PathVariable Long userId);
}
