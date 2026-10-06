package com.example.novabank.account.Service;

import com.example.novabank.account.Config.JwtConfig;
import com.example.novabank.account.Enum.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;


    public String extractUserId(Claims tokenClaims) {
        return tokenClaims.getSubject();
    }

    public String extractTokenType(Claims claims) {
        return claims.get("type", String.class);
    }

    public List<GrantedAuthority> extractRoles(Claims tokenClaims) {
        @SuppressWarnings("unchecked")
        List<String> roleNames = tokenClaims.get("roles", List.class);
        return roleNames.stream()
                .map(UserRole::valueOf)
                .map(userRole -> new SimpleGrantedAuthority(userRole.name()))
                .collect(Collectors.toList());
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecretKey());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
