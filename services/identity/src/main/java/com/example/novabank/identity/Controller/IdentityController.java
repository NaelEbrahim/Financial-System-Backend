package com.example.novabank.identity.Controller;

import com.example.novabank.identity.DTO.Request.*;
import com.example.novabank.identity.Service.IdentityService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/identity")
public class IdentityController {

    private final IdentityService identityService;

    @PostMapping("/register")
    public ResponseEntity<?> userRegister(@RequestBody @Valid RegisterRequest registerRequest) throws JsonProcessingException {
        identityService.userRegister(registerRequest);
        return ResponseEntity.ok(Map.of("message",
                "your registration request has been received. if the provided email address is eligible for registration, account details will be sent to that email shortly"));
    }

    @PostMapping("/register-verify-otp")
    public ResponseEntity<?> userRegisterVerifyOTP(@RequestBody @Valid UserRegisterVerifyOTPRequest userRegisterVerifyOTPRequest) throws JsonProcessingException {
        var response = identityService.userRegisterVerifyOTP(userRegisterVerifyOTPRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/resend-register-otp")
    public ResponseEntity<?> resendRegisterOTP(@RequestBody @Valid ForgotPasswordRequest request) throws JsonProcessingException {
        identityService.reSendRegisterOTPVerification(request.email());
        return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody @Valid LoginRequest loginRequest) {
        var response = identityService.userLogin(loginRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest)
            throws JsonProcessingException {
        identityService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok(Map.of("message",
                "if an account exists for the provided email address, a verification code will be sent shortly"
        ));
    }

    @PutMapping("/reset-forgot-password")
    public ResponseEntity<?> resetForgotPassword(@RequestBody @Valid ResetForgotPasswordRequest resetForgotPasswordRequest)
            throws JsonProcessingException {
        identityService.resetForgotPassword(resetForgotPasswordRequest);
        return ResponseEntity.ok(Map.of("message", "password updated"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> userLogout() {
        var response = identityService.userLogout();
        return ResponseEntity.ok(Map.of("message", response));
    }

    @GetMapping("/get-user-profile")
    public ResponseEntity<?> getUserProfile() {
        var response = identityService.getUserProfile();
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PutMapping("/update-user-profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
        var response = identityService.updateUserProfile(updateProfileRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PutMapping("/update-user-password")
    public ResponseEntity<?> updateUserPassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest) {
        var response = identityService.updateUserPassword(updatePasswordRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken (@RequestBody @Valid RefreshTokenRequest refreshTokenRequest){
        var response = identityService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PostMapping("/request-email-change")
    public ResponseEntity<?> requestEmailChange (@RequestBody @Valid UpdateEmailRequest updateEmailRequest) throws JsonProcessingException {
        var response = identityService.requestUpdateUserEmail(updateEmailRequest);
        return ResponseEntity.ok(Map.of("message", response));
    }

    @PutMapping("/update-user-email")
    public ResponseEntity<?> updateUserEmail (@RequestBody @Valid UserRegisterVerifyOTPRequest updateEmailRequest) throws JsonProcessingException {
        identityService.updateUserEmail(updateEmailRequest);
        return ResponseEntity.ok(Map.of("message", "email updated successfully"));
    }


}
