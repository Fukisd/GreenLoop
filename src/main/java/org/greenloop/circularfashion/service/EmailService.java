package org.greenloop.circularfashion.service;

import java.util.Map;

public interface EmailService {
    void sendSimpleMail(String to, String subject, String text);
    void sendHtmlMail(String to, String subject, String recipientName, String verificationUrl);
    void sendPasswordResetEmail(String email, String token);
    void sendLoginAlert(String to, String recipientName, String ipAddress, String userAgent);
    void sendWelcomeEmail(String email, String firstName);
    void sendNotificationEmail(String to, String subject, String templateName, Map<String, Object> variables);
    void sendVerificationEmail(String email, String token);
    void sendPointsExpiryNotification(String email, Integer points, Integer days);
} 
