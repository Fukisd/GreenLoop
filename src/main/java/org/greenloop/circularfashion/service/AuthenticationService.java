package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.LoginRequest;
import org.greenloop.circularfashion.entity.request.RegisterRequest;
import org.greenloop.circularfashion.entity.response.LoginResponse;

public interface AuthenticationService {
    
    User register(RegisterRequest registerRequest);
    
    LoginResponse login(LoginRequest loginRequest);
    
    String verifyToken(String token);
} 