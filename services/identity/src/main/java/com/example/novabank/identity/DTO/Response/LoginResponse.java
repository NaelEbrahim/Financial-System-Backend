package com.example.novabank.identity.DTO.Response;

public record LoginResponse(
        String accesstoken,

        String refreshtoken
) {
}
