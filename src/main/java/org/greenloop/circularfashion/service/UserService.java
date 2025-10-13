package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.UserUpdateRequest;
import org.greenloop.circularfashion.entity.response.UserDetailResponse;
import org.greenloop.circularfashion.entity.response.UserManagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.UUID;

public interface UserService {

    // User CRUD operations
    UserDetailResponse getUserById(UUID userId);
    
    UserDetailResponse getUserByEmail(String email);
    
    UserDetailResponse getUserByUsername(String username);
    
    UserDetailResponse updateUser(UUID userId, UserUpdateRequest request);
    
    void deleteUser(UUID userId);
    
    void activateUser(UUID userId);
    
    void deactivateUser(UUID userId);
    
    void banUser(UUID userId, String reason);
    
    void unbanUser(UUID userId);
    
    void verifyUser(UUID userId);
    
    // User management
    Page<UserDetailResponse> getAllUsers(Pageable pageable);
    
    Page<UserDetailResponse> searchUsers(String keyword, Pageable pageable);
    
    Page<UserDetailResponse> getUsersByType(User.UserType userType, Pageable pageable);
    
    Page<UserDetailResponse> getUsersByRole(User.Role role, Pageable pageable);
    
    Page<UserDetailResponse> getActiveUsers(Pageable pageable);
    
    Page<UserDetailResponse> getBannedUsers(Pageable pageable);
    
    UserManagementResponse getUserManagementSummary(Pageable pageable);
    
    // User statistics
    Map<String, Object> getUserStatistics(UUID userId);
    
    Long getTotalUsers();
    
    Long getActiveUsersCount();
    
    Long getBannedUsersCount();
    
    Long getVerifiedUsersCount();
    
    Map<String, Long> getUsersByTypeCount();
    
    Map<String, Long> getUsersByRoleCount();
    
    // Profile management
    UserDetailResponse updateUserProfile(UUID userId, UserUpdateRequest request);
    
    UserDetailResponse updateUserAvatar(UUID userId, String avatarUrl);
    
    // User verification
    void sendVerificationEmail(UUID userId);
    
    void verifyEmail(UUID userId, String token);
    
    void verifyPhone(UUID userId, String code);
    
    // Password management
    void changePassword(UUID userId, String oldPassword, String newPassword);
    
    void resetPassword(String email);
    
    void updatePasswordWithToken(String token, String newPassword);
    
    // Social features
    Long getFollowersCount(UUID userId);
    
    Long getFollowingCount(UUID userId);
    
    boolean isFollowing(UUID followerId, UUID followedId);
    
    void followUser(UUID followerId, UUID followedId);
    
    void unfollowUser(UUID followerId, UUID followedId);
    
    // Trust and sustainability scoring
    void updateTrustScore(UUID userId);
    
    void updateSustainabilityScore(UUID userId);
    
    // Helper methods
    User convertToEntity(UserDetailResponse response);
    
    UserDetailResponse convertToDetailResponse(User user);
}









