package com.example.novabank.notification.Service;

import com.example.novabank.notification.DTO.Response.GetNotificationsResponse;
import com.example.novabank.notification.Enum.Type;
import com.example.novabank.notification.Event.consume.*;
import com.example.novabank.notification.Model.NotificationModel;
import com.example.novabank.notification.Repository.NotificationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper;

    private final MailService mailService;


    @KafkaListener(topics = "ACCOUNT_CREATED", groupId = "notification-group")
    public void sendUserRegistrationConfirmation(String payload)
            throws MessagingException, JsonProcessingException {
        AccountCreatedEvent event =
                objectMapper.readValue(
                        payload,
                        AccountCreatedEvent.class
                );

        NotificationModel newNotification = new NotificationModel();
        newNotification.setUserId(event.userId());
        newNotification.setTitle("Welcome To NovaBank");
        newNotification.setContent("Your account has been created successfully");
        newNotification.setType(Type.ACCOUNT_CREATED);

        notificationRepository.save(newNotification);

        mailService.sendAccountCreatedEmail(
                event.email(),
                event.firstname() + " " + event.lastname(),
                event.username()
        );
    }

    @KafkaListener(topics = "FORGOT_PASSWORD_OTP_GENERATED", groupId = "notification-group")
    public void sendOTPForgotPasswordCode(String payload)
            throws MessagingException, JsonProcessingException {

        OTPGeneratedEvent event =
                objectMapper.readValue(
                        payload,
                        OTPGeneratedEvent.class
                );

        mailService.sendOTPForgotPassword(
                event.user_email(),
                event.user_firstname() + " " + event.user_lastname(),
                event.otpCode()
        );
    }

    @KafkaListener(topics = "PASSWORD_CHANGED", groupId = "notification-group")
    public void sendPasswordChangedConfirmation(String payload)
            throws MessagingException, JsonProcessingException {

        PasswordChangedEvent event =
                objectMapper.readValue(
                        payload,
                        PasswordChangedEvent.class
                );

        mailService.sendPasswordChangedConfirmation(
                event.user_email(),
                event.user_firstname() + " " + event.user_lastname(),
                event.date_time().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
        );
    }


    @KafkaListener(topics = "USER_CREATION_PENDING", groupId = "notification-group")
    public void sendRegisterVerifyOTP(String payload)
            throws MessagingException, JsonProcessingException {

        UserCreationPendingEvent event =
                objectMapper.readValue(
                        payload,
                        UserCreationPendingEvent.class
                );

        mailService.sendRegisterVerifyOTP(
                event.email(),
                event.firstname() + " " + event.lastname(),
                event.otp(),
                "REGISTER"
        );
    }

    @KafkaListener(topics = "EMAIL_CHANGE_PENDING", groupId = "notification-group")
    public void sendEmailChangeVerifyOTP(String payload)
            throws MessagingException, JsonProcessingException {

        OTPGeneratedEvent event =
                objectMapper.readValue(
                        payload,
                        OTPGeneratedEvent.class
                );

        mailService.sendRegisterVerifyOTP(
                event.user_email(),
                event.user_firstname() + " " + event.user_lastname(),
                event.otpCode(),
                "EMAIL_CHANGE"
        );
    }

    @Transactional
    @KafkaListener(topics = "EMAIL_CHANGED", groupId = "notification-group")
    public void sendEmailChangedConfirmation(String payload)
            throws MessagingException, JsonProcessingException {

        EmailUpdatedEvent event =
                objectMapper.readValue(
                        payload,
                        EmailUpdatedEvent.class
                );

        NotificationModel newNotification = new NotificationModel();
        newNotification.setUserId(event.user_id());
        newNotification.setTitle("Email Changed");
        newNotification.setContent("your Email has Successfully Changed from " + event.old_email() + " to " + event.new_email());
        newNotification.setType(Type.SYSTEM_ALERT);

        notificationRepository.save(newNotification);


        mailService.sendEmailChangedConfirmation(
                event.old_email(),
                event.new_email(),
                event.user_firstname() + " " + event.user_lastname(),
                event.date_time().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"))
        );
    }

    @Transactional
    @KafkaListener(topics = "TRANSFER_COMPLETED", groupId = "notification-group")
    public void sendTransferConfirmationNotification(String payload)
            throws MessagingException, JsonProcessingException {

        TransferFinishedEvent event =
                objectMapper.readValue(
                        payload,
                        TransferFinishedEvent.class
                );

        NotificationModel senderNotification = new NotificationModel();
        senderNotification.setUserId(event.senderId());
        senderNotification.setTitle("Transfer Completed");
        senderNotification.setContent("you Successfully sent " + event.amount() + "$");
        senderNotification.setType(Type.WITHDRAWAL_COMPLETED);

        notificationRepository.save(senderNotification);


        NotificationModel receiverNotification = new NotificationModel();
        receiverNotification.setUserId(event.receiverId());
        receiverNotification.setTitle("Transfer Completed");
        receiverNotification.setContent("you Successfully Received " + event.amount() + "$");
        receiverNotification.setType(Type.DEPOSIT_COMPLETED);

        notificationRepository.save(receiverNotification);
    }


    @KafkaListener(topics = "TRANSFER_FAILED", groupId = "notification-group")
    public void sendTransferFailedNotification(String payload)
            throws MessagingException, JsonProcessingException {

        TransferFinishedEvent event =
                objectMapper.readValue(
                        payload,
                        TransferFinishedEvent.class
                );

        NotificationModel senderNotification = new NotificationModel();
        senderNotification.setUserId(event.senderId());
        senderNotification.setTitle("Transfer Failed");
        senderNotification.setContent("you can not send " + event.amount() + "$ to " + event.reference_number() + "\nplease try again later");
        senderNotification.setType(Type.SYSTEM_ALERT);

        notificationRepository.save(senderNotification);
    }

    public List<GetNotificationsResponse> getUserNotifications(Pageable pageable) {
        var userId = getUserIdFromContextHolder();
        var notifications = notificationRepository.findByUserId(userId, pageable);

        List<GetNotificationsResponse> userNotifications = new ArrayList<>();
        for (var item : notifications) {
            userNotifications.add(new GetNotificationsResponse(
                    item.getId(),
                    item.getTitle(),
                    item.getContent(),
                    item.getType(),
                    item.getIsRead(),
                    item.getCreatedAt()
            ));
        }
        return userNotifications;
    }

    private Long getUserIdFromContextHolder() {
        return Long.valueOf(String.valueOf(Objects
                .requireNonNull(SecurityContextHolder.getContext()
                        .getAuthentication()).getPrincipal()));
    }


    public boolean deleteNotification(Long id) {
        var userId = getUserIdFromContextHolder();
        var notification = notificationRepository.findById(id).orElse(null);
        if (notification != null && notification.getUserId().equals(userId)) {
            notificationRepository.deleteById(notification.getId());
            return true;
        } else return false;
    }

    public boolean markNotificationRead(Long id) {
        var userId = getUserIdFromContextHolder();
        var notification = notificationRepository.findById(id).orElse(null);
        if (notification != null && notification.getUserId().equals(userId)) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return true;
        } else return false;
    }
}
