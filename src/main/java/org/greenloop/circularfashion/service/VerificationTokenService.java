package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.User;

public interface VerificationTokenService {
    
    String generateVerificationToken(User user);
    
    String verifyToken(String token);
    
    String generatePasswordResetToken(User user);
    
    String resetPasswordWithToken(String token, String newPassword);
    
    void createVerificationToken(User user, String token);
    
    boolean validateVerificationToken(String token);
} 
