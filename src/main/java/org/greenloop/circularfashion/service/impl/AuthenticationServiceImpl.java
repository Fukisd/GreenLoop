package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.LoginRequest;
import org.greenloop.circularfashion.entity.request.RegisterRequest;
import org.greenloop.circularfashion.entity.response.LoginResponse;
import org.greenloop.circularfashion.entity.response.UserResponse;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.security.JwtTokenProvider;
import org.greenloop.circularfashion.service.AuthenticationService;
import org.greenloop.circularfashion.service.EmailService;
import org.greenloop.circularfashion.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        try {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmailOrUsername(),
                        loginRequest.getPassword()
                )
        );

            // Get user details
            User user = userRepository.findByEmailOrUsername(loginRequest.getEmailOrUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if email is verified
            if (!user.getEmailVerified()) {
                throw new RuntimeException("Please verify your email before logging in. Check your inbox for the verification link.");
            }

            // Check if account is active
            if (user.getIsBanned()) {
                throw new RuntimeException("Your account has been banned. Please contact support.");
            }

            if (!user.getIsActive()) {
                throw new RuntimeException("Your account is inactive. Please contact support.");
            }

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

        // Generate JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // Create response
        return LoginResponse.builder()
                .accessToken(token) // Changed from 'token' to 'accessToken'
                .tokenType("Bearer")
                .userId(user.getUserId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .userType(user.getUserType())
                .role(user.getRole())
                .sustainabilityScore(user.getSustainabilityScore().doubleValue())
                .sustainabilityPoints(user.getSustainabilityPoints())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .build();

        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if username already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        // Create new user
        User user = User.builder()
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .phone(registerRequest.getPhone())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .gender(registerRequest.getGender())
                .userType(registerRequest.getUserType())
                .role(User.Role.USER)
                .bio(registerRequest.getBio())
                .isVerified(false)
                .emailVerified(false)
                .phoneVerified(false)
                .isActive(true)
                .isBanned(false)
                .sustainabilityPoints(0)
                .sustainabilityScore(BigDecimal.ZERO)
                .trustScore(BigDecimal.valueOf(5.0))
                .build();
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Generate verification token
        String verificationToken = verificationTokenService.generateVerificationToken(savedUser);
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);
        
        // Return user response
        return UserResponse.builder()
                .userId(savedUser.getUserId().toString())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .username(savedUser.getUsername())
                .phone(savedUser.getPhone())
                .userType(savedUser.getUserType())
                .role(savedUser.getRole())
                .sustainabilityScore(savedUser.getSustainabilityScore().doubleValue())
                .sustainabilityPoints(savedUser.getSustainabilityPoints())
                .emailVerified(savedUser.getEmailVerified())
                .phoneVerified(savedUser.getPhoneVerified())
                .avatarUrl(savedUser.getAvatarUrl())
                .createdAt(savedUser.getCreatedAt().toString())
                .updatedAt(savedUser.getUpdatedAt().toString())
                .build();
    }

    @Override
    public String verifyEmail(String token) {
        try {
            return verificationTokenService.verifyToken(token);
        } catch (Exception e) {
            throw new RuntimeException("Email verification failed: " + e.getMessage());
        }
    }

    @Override
    public LoginResponse refreshToken(String token) {
        try {
            // Extract token from "Bearer <token>"
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            
            if (jwtTokenProvider.validateToken(actualToken)) {
                String username = jwtTokenProvider.getUsernameFromToken(actualToken);
                User user = userRepository.findByEmailOrUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

                // Generate new token
                String newToken = jwtTokenProvider.generateTokenFromUsername(username);

                return LoginResponse.builder()
                    .accessToken(newToken) // Changed from 'token' to 'accessToken'
                    .tokenType("Bearer")
                    .userId(user.getUserId().toString())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .userType(user.getUserType())
                    .role(user.getRole())
                    .sustainabilityScore(user.getSustainabilityScore().doubleValue())
                    .sustainabilityPoints(user.getSustainabilityPoints())
                    .emailVerified(user.getEmailVerified())
                    .phoneVerified(user.getPhoneVerified())
                    .build();
            } else {
                throw new RuntimeException("Invalid token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    public String requestPasswordReset(String email) {
        try {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate reset token
            String resetToken = verificationTokenService.generatePasswordResetToken(user);

            // Send reset email
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);

            return "Password reset email sent successfully";
        } catch (Exception e) {
            throw new RuntimeException("Password reset request failed: " + e.getMessage());
        }
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        try {
            return verificationTokenService.resetPasswordWithToken(token, newPassword);
        } catch (Exception e) {
            throw new RuntimeException("Password reset failed: " + e.getMessage());
        }
    }

    @Override
    public UserResponse getCurrentUser(String token) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtTokenProvider.getUsernameFromToken(actualToken);
            
            User user = userRepository.findByEmailOrUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            return UserResponse.builder()
                .userId(user.getUserId().toString())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone()) // Added phone field
                .userType(user.getUserType())
                .role(user.getRole())
                .sustainabilityScore(user.getSustainabilityScore().doubleValue())
                .sustainabilityPoints(user.getSustainabilityPoints())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt().toString())
                .updatedAt(user.getUpdatedAt().toString())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user profile: " + e.getMessage());
        }
    }

    @Override
    public UserResponse updateProfile(String token, UserResponse userData) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtTokenProvider.getUsernameFromToken(actualToken);
            
            User user = userRepository.findByEmailOrUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Update user fields
            if (userData.getFirstName() != null) {
                user.setFirstName(userData.getFirstName());
            }
            if (userData.getLastName() != null) {
                user.setLastName(userData.getLastName());
            }
            if (userData.getUsername() != null) {
                user.setUsername(userData.getUsername());
            }
            if (userData.getPhone() != null) {
                user.setPhone(userData.getPhone());
            }
            if (userData.getAvatarUrl() != null) {
                user.setAvatarUrl(userData.getAvatarUrl());
            }

            User updatedUser = userRepository.save(user);

            return UserResponse.builder()
                .userId(updatedUser.getUserId().toString())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .username(updatedUser.getUsername())
                .phone(updatedUser.getPhone()) // Added phone field
                .userType(updatedUser.getUserType())
                .role(updatedUser.getRole())
                .sustainabilityScore(updatedUser.getSustainabilityScore().doubleValue())
                .sustainabilityPoints(updatedUser.getSustainabilityPoints())
                .emailVerified(updatedUser.getEmailVerified())
                .phoneVerified(updatedUser.getPhoneVerified())
                .avatarUrl(updatedUser.getAvatarUrl())
                .createdAt(updatedUser.getCreatedAt().toString())
                .updatedAt(updatedUser.getUpdatedAt().toString())
                .build();
        } catch (Exception e) {
            throw new RuntimeException("Profile update failed: " + e.getMessage());
        }
    }

    @Override
    public String changePassword(String token, String currentPassword, String newPassword) {
        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String username = jwtTokenProvider.getUsernameFromToken(actualToken);
            
            User user = userRepository.findByEmailOrUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
                throw new RuntimeException("Current password is incorrect");
            }

            // Validate new password
            if (newPassword == null || newPassword.length() < 8) {
                throw new RuntimeException("New password must be at least 8 characters long");
            }

            // Update password
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            return "Password changed successfully";
        } catch (Exception e) {
            throw new RuntimeException("Password change failed: " + e.getMessage());
        }
    }

    @Override
    public String resendVerificationEmail(String email) {
        try {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getEmailVerified()) {
                throw new RuntimeException("Email is already verified");
            }

            // Generate new verification token
            String verificationToken = verificationTokenService.generateVerificationToken(user);

            // Send verification email
            emailService.sendVerificationEmail(email, verificationToken);

            return "Verification email sent successfully";
        } catch (Exception e) {
            throw new RuntimeException("Failed to resend verification email: " + e.getMessage());
        }
    }

    @Override
    public String forgotPassword(String email) {
        try {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate reset token
            String resetToken = verificationTokenService.generatePasswordResetToken(user);

            // Send reset email
            emailService.sendPasswordResetEmail(email, resetToken);

            return "Password reset email sent successfully";
        } catch (Exception e) {
            throw new RuntimeException("Password reset request failed: " + e.getMessage());
        }
    }
} 
