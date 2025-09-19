package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.VerificationToken;
import org.greenloop.circularfashion.entity.request.LoginRequest;
import org.greenloop.circularfashion.entity.request.RegisterRequest;
import org.greenloop.circularfashion.entity.response.LoginResponse;
import org.greenloop.circularfashion.enums.UserType;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.repository.VerificationTokenRepository;
import org.greenloop.circularfashion.security.JwtTokenProvider;
import org.greenloop.circularfashion.service.AuthenticationService;
import org.greenloop.circularfashion.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public User register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered");
        }

        // Check if username already exists (if provided)
        if (registerRequest.getUsername() != null && 
            userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create new user
        User newUser = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .dateOfBirth(registerRequest.getDateOfBirth())
                .gender(registerRequest.getGender())
                .username(registerRequest.getUsername() != null ? 
                         registerRequest.getUsername() : registerRequest.getEmail())
                .userType(UserType.CONSUMER)
                .isActive(true)
                .emailVerified(false)
                .loginAlertsEnabled(true)
                .sustainabilityScore(0)
                .build();

        User savedUser = userRepository.save(newUser);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser, LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(verificationToken);

        // Send verification email
        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        emailService.sendHtmlMail(
            registerRequest.getEmail(), 
            "Verify your GreenLoop account", 
            registerRequest.getFirstName() + " " + registerRequest.getLastName(), 
            verifyUrl
        );

        return savedUser;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Get user details
        User user = (User) authentication.getPrincipal();

        // Send login alert if enabled
        if (user.isLoginAlertsEnabled()) {
            emailService.sendLoginAlert(
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                "Unknown", // You can get IP from request context
                "Web Browser" // You can get user agent from request context
            );
        }

        // Build login response
        return LoginResponse.builder()
                .token(jwt)
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .userType(user.getUserType())
                .sustainabilityScore(user.getSustainabilityScore())
                .emailVerified(user.isEmailVerified())
                .build();
    }

    @Override
    public String verifyToken(String token) {
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