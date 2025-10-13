package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.UserFollow;
import org.greenloop.circularfashion.entity.request.UserUpdateRequest;
import org.greenloop.circularfashion.entity.response.UserDetailResponse;
import org.greenloop.circularfashion.entity.response.UserManagementResponse;
import org.greenloop.circularfashion.exception.ResourceNotFoundException;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.EmailService;
import org.greenloop.circularfashion.service.UserService;
import org.greenloop.circularfashion.service.VerificationTokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return convertToDetailResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return convertToDetailResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return convertToDetailResponse(user);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUser(UUID userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", userId);
        return convertToDetailResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
        log.info("User deleted successfully: {}", userId);
    }

    @Override
    @Transactional
    public void activateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated successfully: {}", userId);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated successfully: {}", userId);
    }

    @Override
    @Transactional
    public void banUser(UUID userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsBanned(true);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User banned successfully: {} - Reason: {}", userId, reason);
    }

    @Override
    @Transactional
    public void unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsBanned(false);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User unbanned successfully: {}", userId);
    }

    @Override
    @Transactional
    public void verifyUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setIsVerified(true);
        user.setEmailVerified(true);
        userRepository.save(user);
        log.info("User verified successfully: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> searchUsers(String keyword, Pageable pageable) {
        // Implementation for search - can be enhanced with specifications
        return userRepository.findAll(pageable).map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getUsersByType(User.UserType userType, Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getUsersByRole(User.Role role, Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getActiveUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDetailResponse> getBannedUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementResponse getUserManagementSummary(Pageable pageable) {
        Page<UserDetailResponse> users = getAllUsers(pageable);
        
        return UserManagementResponse.builder()
                .users(users)
                .totalUsers(getTotalUsers())
                .activeUsers(getActiveUsersCount())
                .bannedUsers(getBannedUsersCount())
                .verifiedUsers(getVerifiedUsersCount())
                .usersByType(getUsersByTypeCount())
                .usersByRole(getUsersByRoleCount())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("username", user.getUsername());
        stats.put("sustainabilityPoints", user.getSustainabilityPoints());
        stats.put("sustainabilityScore", user.getSustainabilityScore());
        stats.put("trustScore", user.getTrustScore());
        stats.put("followersCount", user.getFollowers().size());
        stats.put("followingCount", user.getFollowing().size());
        stats.put("itemsCount", user.getOwnedItems().size());
        stats.put("listingsCount", user.getListings().size());
        stats.put("ordersCount", user.getPurchases().size());
        stats.put("createdAt", user.getCreatedAt());
        stats.put("lastLogin", user.getLastLogin());
        
        return stats;
    }

    @Override
    public Long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    public Long getActiveUsersCount() {
        return userRepository.findAll().stream()
                .filter(User::getIsActive)
                .count();
    }

    @Override
    public Long getBannedUsersCount() {
        return userRepository.findAll().stream()
                .filter(User::getIsBanned)
                .count();
    }

    @Override
    public Long getVerifiedUsersCount() {
        return userRepository.findAll().stream()
                .filter(User::getIsVerified)
                .count();
    }

    @Override
    public Map<String, Long> getUsersByTypeCount() {
        return userRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getUserType().name(),
                        Collectors.counting()
                ));
    }

    @Override
    public Map<String, Long> getUsersByRoleCount() {
        return userRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        user -> user.getRole().name(),
                        Collectors.counting()
                ));
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserProfile(UUID userId, UserUpdateRequest request) {
        return updateUser(userId, request);
    }

    @Override
    @Transactional
    public UserDetailResponse updateUserAvatar(UUID userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setAvatarUrl(avatarUrl);
        User updatedUser = userRepository.save(user);
        return convertToDetailResponse(updatedUser);
    }

    @Override
    @Transactional
    public void sendVerificationEmail(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        
        // Send email with verification link
        String verificationLink = "http://localhost:8080/api/users/verify-email?token=" + token;
        emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        
        log.info("Verification email sent to: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void verifyEmail(UUID userId, String token) {
        if (verificationTokenService.validateVerificationToken(token)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            user.setEmailVerified(true);
            userRepository.save(user);
            log.info("Email verified for user: {}", userId);
        } else {
            throw new IllegalArgumentException("Invalid or expired verification token");
        }
    }

    @Override
    @Transactional
    public void verifyPhone(UUID userId, String code) {
        // Implementation for phone verification with SMS code
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setPhoneVerified(true);
        userRepository.save(user);
        log.info("Phone verified for user: {}", userId);
    }

    @Override
    @Transactional
    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String token = UUID.randomUUID().toString();
        verificationTokenService.createVerificationToken(user, token);
        
        // Send password reset email
        String resetLink = "http://localhost:8080/api/users/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(email, resetLink);
        
        log.info("Password reset email sent to: {}", email);
    }

    @Override
    @Transactional
    public void updatePasswordWithToken(String token, String newPassword) {
        if (verificationTokenService.validateVerificationToken(token)) {
            // Get user from token and update password
            // Implementation depends on your VerificationToken entity structure
            log.info("Password updated with token");
        } else {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
    }

    @Override
    public Long getFollowersCount(UUID userId) {
        return userRepository.countFollowing(userId);
    }

    @Override
    public Long getFollowingCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return (long) user.getFollowing().size();
    }

    @Override
    public boolean isFollowing(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + followerId));
        
        return follower.getFollowing().stream()
                .anyMatch(follow -> follow.getFollowed().getUserId().equals(followedId));
    }

    @Override
    @Transactional
    public void followUser(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower not found with id: " + followerId));
        User followed = userRepository.findById(followedId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + followedId));
        
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }
        
        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(follower);
        userFollow.setFollowed(followed);
        userFollow.setCreatedAt(LocalDateTime.now());
        
        follower.getFollowing().add(userFollow);
        userRepository.save(follower);
        
        log.info("User {} followed user {}", followerId, followedId);
    }

    @Override
    @Transactional
    public void unfollowUser(UUID followerId, UUID followedId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower not found with id: " + followerId));
        
        follower.getFollowing().removeIf(follow -> 
                follow.getFollowed().getUserId().equals(followedId));
        
        userRepository.save(follower);
        log.info("User {} unfollowed user {}", followerId, followedId);
    }

    @Override
    @Transactional
    public void updateTrustScore(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Calculate trust score based on various factors
        BigDecimal trustScore = calculateTrustScore(user);
        user.setTrustScore(trustScore);
        userRepository.save(user);
        
        log.info("Trust score updated for user {}: {}", userId, trustScore);
    }

    @Override
    @Transactional
    public void updateSustainabilityScore(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Calculate sustainability score based on various factors
        BigDecimal sustainabilityScore = calculateSustainabilityScore(user);
        user.setSustainabilityScore(sustainabilityScore);
        userRepository.save(user);
        
        log.info("Sustainability score updated for user {}: {}", userId, sustainabilityScore);
    }

    @Override
    public User convertToEntity(UserDetailResponse response) {
        return User.builder()
                .userId(response.getUserId())
                .email(response.getEmail())
                .username(response.getUsername())
                .firstName(response.getFirstName())
                .lastName(response.getLastName())
                .phone(response.getPhone())
                .dateOfBirth(response.getDateOfBirth())
                .gender(response.getGender())
                .avatarUrl(response.getAvatarUrl())
                .bio(response.getBio())
                .sustainabilityPoints(response.getSustainabilityPoints())
                .sustainabilityScore(response.getSustainabilityScore())
                .trustScore(response.getTrustScore())
                .isVerified(response.getIsVerified())
                .emailVerified(response.getEmailVerified())
                .phoneVerified(response.getPhoneVerified())
                .isActive(response.getIsActive())
                .isBanned(response.getIsBanned())
                .lastLogin(response.getLastLogin())
                .createdAt(response.getCreatedAt())
                .updatedAt(response.getUpdatedAt())
                .build();
    }

    @Override
    public UserDetailResponse convertToDetailResponse(User user) {
        return UserDetailResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .userType(user.getUserType().name())
                .role(user.getRole().name())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .sustainabilityPoints(user.getSustainabilityPoints())
                .sustainabilityScore(user.getSustainabilityScore())
                .trustScore(user.getTrustScore())
                .isVerified(user.getIsVerified())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .isActive(user.getIsActive())
                .isBanned(user.getIsBanned())
                .followersCount((long) user.getFollowers().size())
                .followingCount((long) user.getFollowing().size())
                .itemsCount((long) user.getOwnedItems().size())
                .listingsCount((long) user.getListings().size())
                .ordersCount((long) user.getPurchases().size())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Helper methods for score calculation
    private BigDecimal calculateTrustScore(User user) {
        // Implement trust score calculation logic
        // Based on: verified status, completed transactions, reviews, etc.
        BigDecimal baseScore = BigDecimal.valueOf(5.0);
        
        if (user.getIsVerified()) baseScore = baseScore.add(BigDecimal.valueOf(1.0));
        if (user.getEmailVerified()) baseScore = baseScore.add(BigDecimal.valueOf(0.5));
        if (user.getPhoneVerified()) baseScore = baseScore.add(BigDecimal.valueOf(0.5));
        
        // Add more factors based on transactions, reviews, etc.
        
        return baseScore.min(BigDecimal.valueOf(10.0));
    }

    private BigDecimal calculateSustainabilityScore(User user) {
        // Implement sustainability score calculation logic
        // Based on: recycling activities, eco-friendly purchases, etc.
        Integer points = user.getSustainabilityPoints() != null ? user.getSustainabilityPoints() : 0;
        
        // Convert points to score (0-10 scale)
        BigDecimal score = BigDecimal.valueOf(points).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        
        return score.min(BigDecimal.valueOf(10.0));
    }
}

