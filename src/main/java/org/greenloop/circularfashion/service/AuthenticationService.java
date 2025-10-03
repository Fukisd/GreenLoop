package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.request.LoginRequest;
import org.greenloop.circularfashion.entity.request.RegisterRequest;
import org.greenloop.circularfashion.entity.response.LoginResponse;
import org.greenloop.circularfashion.entity.response.UserResponse;

public interface AuthenticationService {
    
    LoginResponse login(LoginRequest loginRequest);
    
    UserResponse register(RegisterRequest registerRequest);
    
    String verifyEmail(String token);
    
    LoginResponse refreshToken(String token);
    
    String requestPasswordReset(String email);
    
    String resetPassword(String token, String newPassword);
    
    UserResponse getCurrentUser(String token);
    
    UserResponse updateProfile(String token, UserResponse userData);
    
    // Add these missing methods
    String changePassword(String token, String currentPassword, String newPassword);
    
    String resendVerificationEmail(String email);
    
    String forgotPassword(String email);
} 
