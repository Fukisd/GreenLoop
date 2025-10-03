package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.VerificationToken;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.repository.VerificationTokenRepository;
import org.greenloop.circularfashion.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository; // Added back UserRepository

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String generateVerificationToken(User user) {
        // Delete any existing verification tokens for this user
        verificationTokenRepository.deleteByUserUserId(user.getUserId());
        
        // Create new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public String verifyToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setIsVerified(true);
        
        userRepository.save(user); // Save the updated user

        verificationTokenRepository.delete(verificationToken);
        return "Email verified successfully";
    }

    @Override
    @Transactional
    public String generatePasswordResetToken(User user) {
        // Delete any existing password reset tokens for this user
        verificationTokenRepository.deleteByUserUserId(user.getUserId());
        
        // Create new token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public String resetPasswordWithToken(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        User user = verificationToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        
        userRepository.save(user); // Save the updated user
        
        verificationTokenRepository.delete(verificationToken);
        return "Password reset successfully";
    }
} 
