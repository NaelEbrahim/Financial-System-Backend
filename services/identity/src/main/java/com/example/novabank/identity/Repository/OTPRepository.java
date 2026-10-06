package com.example.novabank.identity.Repository;

import com.example.novabank.identity.Model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    Optional<OTP> findByUserEmail(String userEmail);

    void deleteAllByUserEmail (String email);

}
