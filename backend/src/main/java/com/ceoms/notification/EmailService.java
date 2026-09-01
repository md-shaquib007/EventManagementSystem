package com.ceoms.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:ceoms@college.edu}")
    private String fromEmail;

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.warn("Email send failed to {}: {}", to, e.getMessage());
        }
    }

    public void sendNotificationEmail(String to, String title, String message) {
        sendEmail(to, "[CEOMS] " + title, message);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        sendEmail(to, "[CEOMS] Password Reset",
                "Click the link to reset your password: " + resetLink + "\nThis link expires in 1 hour.");
    }

    public void sendVerificationEmail(String to, String verifyLink) {
        sendEmail(to, "[CEOMS] Email Verification",
                "Click the link to verify your email: " + verifyLink);
    }
}
