package com.example.novabank.account.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "identity-service",
        url = "${application.config.identity-url}"
)
public interface IdentityClient {

    @GetMapping("/get-user-by-id/{userId}")
    String isUserIdCorrect(@PathVariable Long userId);

}
