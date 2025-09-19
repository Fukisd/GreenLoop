package org.greenloop.circularfashion.service;

public interface EmailService {
    
    void sendHtmlMail(String to, String subject, String fullName, String verifyUrl);
    
    void sendLoginAlert(String to, String fullName, String ipAddress, String userAgent);
} 