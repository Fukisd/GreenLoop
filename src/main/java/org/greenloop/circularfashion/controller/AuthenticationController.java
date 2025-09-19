package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.LoginRequest;
import org.greenloop.circularfashion.entity.request.RegisterRequest;
import org.greenloop.circularfashion.entity.response.LoginResponse;
import org.greenloop.circularfashion.entity.response.UserResponse;
import org.greenloop.circularfashion.mapper.UserMapper;
import org.greenloop.circularfashion.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Create a new user account and send verification email")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User savedUser = authenticationService.register(registerRequest);
        UserResponse userResponse = userMapper.toUserResponse(savedUser);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authenticationService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify email", description = "Verify user's email address using verification token")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        String result = authenticationService.verifyToken(token);
        if (result.contains("successfully")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
} 