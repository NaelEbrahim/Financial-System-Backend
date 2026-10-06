package com.example.novabank.account.Controller;

import com.example.novabank.account.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountController {

    private final AccountService accountService;


    @GetMapping("/get-account-info")
    public ResponseEntity<?> getAccountInfo() {
        var response = accountService.getAccountInfo();
        return ResponseEntity.ok(Map.of("message", response));
    }


}
