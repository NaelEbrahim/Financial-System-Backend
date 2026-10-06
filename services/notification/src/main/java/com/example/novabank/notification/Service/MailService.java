package com.example.novabank.notification.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Async
    public void sendAccountCreatedEmail(String email, String fullName, String username)
            throws MessagingException {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("username", username);

        String html = templateEngine.process("account-created", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom("no-reply@novabank.com");
        helper.setSubject("Welcome to NovaBank");
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Async
    public void sendOTPForgotPassword(String email, String fullName, String verifyCode)
            throws MessagingException {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("verifyCode", verifyCode);

        String html = templateEngine.process("otp-forgot-password-sent", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom("no-reply@novabank.com");
        helper.setSubject("NovaBank Password Reset Verification Code");
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Async
    public void sendPasswordChangedConfirmation(String email, String fullName, String dateTime)
            throws MessagingException {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("changeDateTime", dateTime);

        String html = templateEngine.process("password-changed", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom("no-reply@novabank.com");
        helper.setSubject("Password Changed Alert");
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Async
    public void sendRegisterVerifyOTP(String email, String fullName, String verifyCode, String type)
            throws MessagingException {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("verifyCode", verifyCode);
        context.setVariable("otpType", type);

        String html = templateEngine.process("register-verify-otp", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setFrom("no-reply@novabank.com");
        helper.setSubject("Email Verification");
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Async
    public void sendEmailChangedConfirmation(String oldEmail, String newEmail, String fullName, String dateTime)
            throws MessagingException {

        Context context = new Context();

        context.setVariable("fullName", fullName);
        context.setVariable("newEmail", newEmail);
        context.setVariable("changedAt", dateTime);

        String html = templateEngine.process("email-changed", context);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(oldEmail);
        helper.setFrom("no-reply@novabank.com");
        helper.setSubject("Email Changed Alert");
        helper.setText(html, true);

        mailSender.send(message);
    }

}
