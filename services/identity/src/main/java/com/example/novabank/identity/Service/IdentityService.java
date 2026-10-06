package com.example.novabank.identity.Service;

import com.example.novabank.identity.Config.SecurityConfig;
import com.example.novabank.identity.DTO.Request.*;
import com.example.novabank.identity.DTO.Response.GetUserProfileResponse;
import com.example.novabank.identity.DTO.Response.LoginResponse;
import com.example.novabank.identity.Enum.OutBoxEventType;
import com.example.novabank.identity.Enum.UserRole;
import com.example.novabank.identity.Enum.UserStatus;
import com.example.novabank.identity.Event.produce.*;
import com.example.novabank.identity.Exception.CustomException;
import com.example.novabank.identity.Model.OTP;
import com.example.novabank.identity.Model.UserModel;
import com.example.novabank.identity.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class IdentityService {

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final User_RoleRepository userRoleRepository;

    private final OTPRepository otpRepository;

    private final SecurityConfig securityConfig;

    private final JwtService jwtService;

    private final OutboxService outboxService;

    private final GeneratorService generatorService;


    public void userRegister(RegisterRequest registerRequest) throws JsonProcessingException {
        if (!profileRepository.existsByEmail(registerRequest.email())) {
            var newEvent = new RegistrationRequestedEvent(
                    registerRequest.email(),
                    registerRequest.firstname(),
                    registerRequest.lastname(),
                    securityConfig.passwordEncoder().encode(registerRequest.pin()),
                    securityConfig.passwordEncoder().encode(registerRequest.password())
            );

            outboxService.saveEvent(OutBoxEventType.REGISTRATION_REQUESTED, newEvent);
        }
    }

    @Transactional
    public String userRegisterVerifyOTP(UserRegisterVerifyOTPRequest userRegisterVerifyOTPRequest) throws JsonProcessingException {
        var otp = otpRepository.findByUserEmail(userRegisterVerifyOTPRequest.email()).orElse(null);

        if (otp != null) {
            boolean isCorrect = securityConfig.passwordEncoder()
                    .matches(userRegisterVerifyOTPRequest.verifycode(), otp.getCode());

            boolean isValid = !otp.getCreatedAt().plus(Duration.ofMinutes(10))
                    .isBefore(Instant.now()) && otp.getAttemptCount() < 3;

            if (isCorrect && isValid) {
                var userProfile = profileRepository.findByEmail(userRegisterVerifyOTPRequest.email()).orElse(null);
                if (userProfile != null) {
                    var user = userProfile.getUser();
                    if (user.getStatus().equals(UserStatus.PENDING)) {
                        user.setStatus(UserStatus.ACTIVE);
                        otpRepository.deleteAllByUserEmail(userRegisterVerifyOTPRequest.email());

                        var newEvent = new UserCreatedEvent(
                                user.getId(),
                                user.getUsername(),
                                userProfile.getEmail(),
                                userProfile.getFirstname(),
                                userProfile.getLastname()
                        );

                        outboxService.saveEvent(OutBoxEventType.USER_CREATED, newEvent);
                        return "email verified successfully, check your email in few seconds";
                    }
                } else {
                    throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
                }
            }
            if (!isCorrect) {
                otp.setAttemptCount(otp.getAttemptCount() + 1);
            }
            if (!isValid) {
                otpRepository.deleteAllByUserEmail(userRegisterVerifyOTPRequest.email());
            }
        }
        throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
    }

    @Transactional
    public void reSendRegisterOTPVerification(String userEmail) throws JsonProcessingException {
        var userProfile = profileRepository.findByEmail(userEmail).orElse(null);
        if (userProfile != null && userProfile.getUser().getStatus().equals(UserStatus.PENDING)) {
            otpRepository.deleteAllByUserEmail(userProfile.getEmail());
            var newOTP = new OTP();
            String code = generatorService.generateOtp();
            newOTP.setCode(securityConfig.passwordEncoder().encode(code));
            newOTP.setUserEmail(userProfile.getEmail());
            otpRepository.save(newOTP);

            var newEvent = new UserCreationPendingEvent(
                    userProfile.getEmail(),
                    userProfile.getFirstname(),
                    userProfile.getLastname(),
                    code
            );
            outboxService.saveEvent(OutBoxEventType.USER_CREATION_PENDING, newEvent);
        }
    }


    public LoginResponse userLogin(LoginRequest loginRequest) {
        UserModel user = userRepository.findByUsername(loginRequest.username()).orElse(null);

        if (user != null && user.getStatus().equals(UserStatus.ACTIVE) && securityConfig.passwordEncoder().matches(loginRequest.password(), user.getPassword())) {
            List<UserRole> userUserRoles = userRoleRepository.findByUserId(user.getId());
            String accessToken = jwtService.generateAccessToken(
                    user.getId().toString(),
                    userUserRoles
            );
            String refreshToken = jwtService.generateRefreshToken(
                    user.getId().toString()
            );
            user.setRefreshtoken(refreshToken);
            userRepository.save(user);

            return new LoginResponse(accessToken, refreshToken);
        } else {
            throw new CustomException("invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }


    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest)
            throws JsonProcessingException {
        var userProfile = profileRepository.findByEmail(forgotPasswordRequest.email()).orElse(null);

        if (userProfile != null && userProfile.getUser().getStatus().equals(UserStatus.ACTIVE)) {
            otpRepository.deleteAllByUserEmail(forgotPasswordRequest.email());
            var newOTP = new OTP();
            String code = generatorService.generateOtp();
            newOTP.setCode(securityConfig.passwordEncoder().encode(code));
            newOTP.setUserEmail(userProfile.getEmail());
            otpRepository.save(newOTP);

            var newEvent = new OTPGeneratedEvent(
                    userProfile.getEmail(),
                    userProfile.getFirstname(),
                    userProfile.getLastname(),
                    code
            );

            outboxService.saveEvent(OutBoxEventType.FORGOT_PASSWORD_OTP_GENERATED, newEvent);
        }
    }

    @Transactional(noRollbackFor = CustomException.class)
    public void resetForgotPassword(ResetForgotPasswordRequest resetForgotPasswordRequest)
            throws JsonProcessingException {
        var otp = otpRepository.findByUserEmail(resetForgotPasswordRequest.email()).orElse(null);

        if (otp != null) {
            boolean isCorrect = securityConfig.passwordEncoder()
                    .matches(resetForgotPasswordRequest.verifycode(), otp.getCode());

            boolean isValid = !otp.getCreatedAt().plus(Duration.ofMinutes(10))
                    .isBefore(Instant.now()) && otp.getAttemptCount() < 3;

            if (isCorrect && isValid) {
                var userProfile = profileRepository.findByEmail(resetForgotPasswordRequest.email()).orElse(null);
                if (userProfile != null) {
                    userProfile.getUser().setPassword(securityConfig.passwordEncoder().encode(resetForgotPasswordRequest.password()));
                    otpRepository.deleteAllByUserEmail(otp.getUserEmail());

                    var newEvent = new PasswordChangedEvent(
                            userProfile.getEmail(),
                            userProfile.getFirstname(),
                            userProfile.getLastname(),
                            LocalDateTime.now()
                    );

                    outboxService.saveEvent(OutBoxEventType.PASSWORD_CHANGED, newEvent);
                    return;
                } else {
                    throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
                }
            }
            if (!isCorrect) {
                otp.setAttemptCount(otp.getAttemptCount() + 1);
            }
            if (!isValid) {
                otpRepository.deleteAllByUserEmail(resetForgotPasswordRequest.email());
            }
        }
        throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
    }


    @Transactional
    public String userLogout() {
        var userId = getUserIdFromContextHolder();

        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        user.setRefreshtoken(null);
        return "logged out successfully";
    }

    public GetUserProfileResponse getUserProfile() {
        var userId = getUserIdFromContextHolder();
        var userProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        return new GetUserProfileResponse(
                userProfile.getFirstname(),
                userProfile.getLastname(),
                userProfile.getEmail(),
                userProfile.getCreatedAt(),
                userProfile.getUpdatedAt()
        );
    }

    private Long getUserIdFromContextHolder() {
        return Long.valueOf(String.valueOf(Objects
                .requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication()).getPrincipal()));
    }

    @Transactional
    public String updateUserProfile(UpdateProfileRequest updateProfileRequest) {
        var userId = getUserIdFromContextHolder();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        if (!securityConfig.passwordEncoder().matches(updateProfileRequest.password(), user.getPassword())) {
            throw new CustomException("wrong password", HttpStatus.BAD_REQUEST);
        }

        var userProfile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        var isUpdated = false;
        if (!userProfile.getFirstname().equalsIgnoreCase(updateProfileRequest.firstname())) {
            isUpdated = true;
            userProfile.setFirstname(updateProfileRequest.firstname());
        }
        if (!userProfile.getLastname().equalsIgnoreCase(updateProfileRequest.lastname())) {
            isUpdated = true;
            userProfile.setLastname(updateProfileRequest.lastname());
        }

        if (!isUpdated) {
            return "no changes detected";
        }

        userProfile.setUpdatedAt(LocalDateTime.now());
        return "profile updated successfully";
    }

    @Transactional
    public String updateUserPassword(UpdatePasswordRequest updatePasswordRequest) {
        if (!updatePasswordRequest.newpassword().equals(updatePasswordRequest.confirmpassword())) {
            throw new CustomException("password confirmation does not match", HttpStatus.BAD_REQUEST);
        }

        var userId = getUserIdFromContextHolder();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        if (!securityConfig.passwordEncoder().matches(updatePasswordRequest.oldpassword(), user.getPassword())) {
            throw new CustomException("old password incorrect", HttpStatus.BAD_REQUEST);
        }

        if (securityConfig.passwordEncoder().matches(updatePasswordRequest.newpassword(), user.getPassword())) {
            throw new CustomException("new password must be different from current password", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(securityConfig.passwordEncoder().encode(updatePasswordRequest.newpassword()));
        return "password updated successfully";
    }

    @Transactional
    public String requestUpdateUserEmail(UpdateEmailRequest updateEmailRequest) throws JsonProcessingException {
        var userId = getUserIdFromContextHolder();
        var user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        var profile = profileRepository.findByUserId(userId).orElseThrow(
                () -> new CustomException(
                        "something went wrong, try again later",
                        HttpStatus.BAD_REQUEST));

        if (!securityConfig.passwordEncoder().matches(updateEmailRequest.password(), user.getPassword())) {
            throw new CustomException("password incorrect", HttpStatus.BAD_REQUEST);
        }

        if (!profile.getEmail().equalsIgnoreCase(updateEmailRequest.newemail())) {
            if (!profileRepository.existsByEmail(updateEmailRequest.newemail())) {
                // Send Verify OTP
                otpRepository.deleteAllByUserEmail(updateEmailRequest.newemail());
                var newOTP = new OTP();
                String code = generatorService.generateOtp();
                newOTP.setCode(securityConfig.passwordEncoder().encode(code));
                newOTP.setUserEmail(updateEmailRequest.newemail());
                otpRepository.save(newOTP);

                var newEvent = new OTPGeneratedEvent(
                        updateEmailRequest.newemail(),
                        profile.getFirstname(),
                        profile.getLastname(),
                        code
                );

                outboxService.saveEvent(OutBoxEventType.EMAIL_CHANGE_PENDING, newEvent);
            }
            return "if new email eligible to use, OTP will sent shortly";
        }
        throw new CustomException("please enter different email", HttpStatus.BAD_REQUEST);
    }


    public String getUserPINByUserId(Long userId) {
        var user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getStatus().equals(UserStatus.ACTIVE)) {
            return user.getPin();
        } else
            return null;
    }

    public LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        var user = userRepository.findByRefreshtoken(refreshTokenRequest.refreshtoken()).orElse(null);
        var claims = jwtService.extractAllClaims(refreshTokenRequest.refreshtoken());
        if (user != null && claims != null) {
            List<UserRole> userUserRoles = userRoleRepository.findByUserId(user.getId());
            String accessToken = jwtService.generateAccessToken(
                    user.getId().toString(),
                    userUserRoles
            );
            String refreshToken = jwtService.generateRefreshToken(
                    user.getId().toString()
            );
            user.setRefreshtoken(refreshToken);
            userRepository.save(user);

            return new LoginResponse(accessToken, refreshToken);
        } else {
            throw new CustomException("invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional(noRollbackFor = CustomException.class)
    public void updateUserEmail(UserRegisterVerifyOTPRequest userRegisterVerifyOTPRequest)
            throws JsonProcessingException {
        var otp = otpRepository.findByUserEmail(userRegisterVerifyOTPRequest.email()).orElse(null);

        var userId = getUserIdFromContextHolder();
        var profile = profileRepository.findByUserId(userId).orElse(null);

        if (otp != null && profile != null) {
            boolean isCorrect = securityConfig.passwordEncoder()
                    .matches(userRegisterVerifyOTPRequest.verifycode(), otp.getCode());

            boolean isValid = !otp.getCreatedAt().plus(Duration.ofMinutes(10))
                    .isBefore(Instant.now()) && otp.getAttemptCount() < 3;

            if (isCorrect && isValid) {
                String oldEmail = profile.getEmail();
                if (!profileRepository.existsByEmail(otp.getUserEmail())) {
                    profile.setEmail(otp.getUserEmail());
                    profile.setUpdatedAt(LocalDateTime.now());
                    otpRepository.deleteAllByUserEmail(otp.getUserEmail());

                    var newEvent = new EmailUpdatedEvent(
                            userId,
                            oldEmail,
                            profile.getEmail(),
                            profile.getFirstname(),
                            profile.getLastname(),
                            LocalDateTime.now()
                    );

                    outboxService.saveEvent(OutBoxEventType.EMAIL_CHANGED, newEvent);
                    return;
                } else {
                    throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
                }
            }
            if (!isCorrect) {
                otp.setAttemptCount(otp.getAttemptCount() + 1);
            }
            if (!isValid) {
                otpRepository.deleteAllByUserEmail(userRegisterVerifyOTPRequest.email());
            }
        }
        throw new CustomException("something went wrong, try again later", HttpStatus.BAD_REQUEST);
    }


}
