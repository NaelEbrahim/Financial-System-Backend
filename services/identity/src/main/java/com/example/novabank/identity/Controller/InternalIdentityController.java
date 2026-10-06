package com.example.novabank.identity.Controller;

import com.example.novabank.identity.Service.IdentityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/identity")
public class InternalIdentityController {

    private final IdentityService identityService;

    @GetMapping("/get-user-by-id/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") Long userId) {
        var response = identityService.getUserPINByUserId(userId);
        return ResponseEntity.ok(response);
    }

}
