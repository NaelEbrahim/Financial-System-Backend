package com.example.novabank.account.Controller;

import com.example.novabank.account.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/account")
public class InternalAccountController {

    private final AccountService accountService;

    @GetMapping("/find-account-by-wallet-key/{walletKey}")
    public ResponseEntity<?> findAccountByWalletKey(@PathVariable String walletKey) {
        var response = accountService.findAccountByWalletKey(walletKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/find-account-by-user-id/{userId}")
    public ResponseEntity<?> findAccountByUserId(@PathVariable Long userId) {
        var response = accountService.findAccountByUserId(userId);
        return ResponseEntity.ok(response);
    }
}
