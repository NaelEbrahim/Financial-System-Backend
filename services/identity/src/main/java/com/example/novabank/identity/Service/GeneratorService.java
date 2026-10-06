package com.example.novabank.identity.Service;

import com.example.novabank.identity.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GeneratorService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final String CHAR_POOL = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final UserRepository userRepository;

    private String randomUsername() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int index = RANDOM.nextInt(CHAR_POOL.length());
            sb.append(CHAR_POOL.charAt(index));
        }
        return sb.toString();
    }

    public String generateUsername() {
        String username;
        do {
            username = randomUsername();
        } while (userRepository.existsByUsername(username));
        return username;
    }

    public String generateOtp() {
        return String.valueOf(
                ThreadLocalRandom.current()
                        .nextInt(1000, 10000)
        );
    }

}
