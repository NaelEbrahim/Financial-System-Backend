package com.example.novabank.identity.Service;

import com.example.novabank.identity.Config.SecurityConfig;
import com.example.novabank.identity.Enum.OutBoxEventType;
import com.example.novabank.identity.Event.produce.RegistrationRequestedEvent;
import com.example.novabank.identity.Event.produce.UserCreationPendingEvent;
import com.example.novabank.identity.Model.OTP;
import com.example.novabank.identity.Model.ProfileModel;
import com.example.novabank.identity.Model.UserModel;
import com.example.novabank.identity.Repository.OTPRepository;
import com.example.novabank.identity.Repository.ProfileRepository;
import com.example.novabank.identity.Repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EventHandler {

    private final UserRepository userRepository;

    private final ProfileRepository profileRepository;

    private final OTPRepository otpRepository;

    private final GeneratorService generatorService;

    private final OutboxService outboxService;

    private final SecurityConfig securityConfig;

    private final ObjectMapper objectMapper;

    private final IdentityService identityService;


    @Transactional
    @KafkaListener(topics = "REGISTRATION_REQUESTED", groupId = "identity-group")
    public void consumeRegisterEvent(String payload) throws JsonProcessingException {
        RegistrationRequestedEvent event =
                objectMapper.readValue(
                        payload,
                        RegistrationRequestedEvent.class
                );
        if (!profileRepository.existsByEmail(event.email())) {

            UserModel user = new UserModel();
            user.setUsername(generatorService.generateUsername());
            user.setPassword(event.password());
            user.setPin(event.pin());
            user = userRepository.save(user);

            ProfileModel profile = new ProfileModel();
            profile.setUser(user);
            profile.setEmail(event.email());
            profile.setFirstname(event.firstname());
            profile.setLastname(event.lastname());
            profileRepository.save(profile);

            otpRepository.deleteAllByUserEmail(event.email());
            var newOTP = new OTP();
            String code = generatorService.generateOtp();
            newOTP.setCode(securityConfig.passwordEncoder().encode(code));
            newOTP.setUserEmail(event.email());
            otpRepository.save(newOTP);

            var newEvent = new UserCreationPendingEvent(
                    event.email(),
                    event.firstname(),
                    event.lastname(),
                    code
            );

            outboxService.saveEvent(OutBoxEventType.USER_CREATION_PENDING, newEvent);
        }
    }

}
