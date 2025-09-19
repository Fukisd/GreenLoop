package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.VerificationToken;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.repository.VerificationTokenRepository;
import org.greenloop.circularfashion.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String verify(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return "Verification token has expired";
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);

        return "Account verified successfully. You can now log in.";
    }
} 