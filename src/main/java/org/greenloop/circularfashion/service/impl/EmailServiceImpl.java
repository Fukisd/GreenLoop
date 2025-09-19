package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void sendHtmlMail(String to, String subject, String fullName, String verifyUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("noreply@greenloop.com");

            // Create context for template
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("verifyUrl", verifyUrl);

            // Process template and send email
            String htmlContent = templateEngine.process("email-verification", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendLoginAlert(String to, String fullName, String ipAddress, String userAgent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setTo(to);
            helper.setSubject("New Login Alert - GreenLoop");
            helper.setFrom("noreply@greenloop.com");

            // Create context for template
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("ipAddress", ipAddress);
            context.setVariable("userAgent", userAgent);
            context.setVariable("loginTime", java.time.LocalDateTime.now().toString());

            // Process template and send email
            String htmlContent = templateEngine.process("login-alert", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send login alert email", e);
        }
    }
} 