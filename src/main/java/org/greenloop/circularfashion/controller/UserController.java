package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.UserUpdateRequest;
import org.greenloop.circularfashion.entity.response.ApiResponse;
import org.greenloop.circularfashion.entity.response.UserDetailResponse;
import org.greenloop.circularfashion.entity.response.UserManagementResponse;
import org.greenloop.circularfashion.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for comprehensive user management")
public class UserController {

    private final UserService userService;

    // ==================== User Retrieval ====================
    
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable UUID userId) {
        UserDetailResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserByEmail(@PathVariable String email) {
        UserDetailResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a user by their username")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserByUsername(@PathVariable String username) {
        UserDetailResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .success(true)
                .message("User retrieved successfully")
                .data(user)
                .build());
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> getAllUsers(Pageable pageable) {
        Page<UserDetailResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    // ==================== User Update ====================
    
    @PutMapping("/{userId}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDetailResponse updatedUser = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .build());
    }

    @PatchMapping("/{userId}/avatar")
    @Operation(summary = "Update user avatar", description = "Update user's avatar URL")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateUserAvatar(
            @PathVariable UUID userId,
            @RequestParam String avatarUrl) {
        UserDetailResponse updatedUser = userService.updateUserAvatar(userId, avatarUrl);
        return ResponseEntity.ok(ApiResponse.<UserDetailResponse>builder()
                .success(true)
                .message("Avatar updated successfully")
                .data(updatedUser)
                .build());
    }

    // ==================== User Management ====================
    
    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user", description = "Delete a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted successfully")
                .build());
    }

    @PatchMapping("/{userId}/activate")
    @Operation(summary = "Activate user", description = "Activate a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateUser(@PathVariable UUID userId) {
        userService.activateUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User activated successfully")
                .build());
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(summary = "Deactivate user", description = "Deactivate a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(@PathVariable UUID userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deactivated successfully")
                .build());
    }

    @PatchMapping("/{userId}/ban")
    @Operation(summary = "Ban user", description = "Ban a user account")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<Void>> banUser(
            @PathVariable UUID userId,
            @RequestParam String reason) {
        userService.banUser(userId, reason);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User banned successfully")
                .build());
    }

    @PatchMapping("/{userId}/unban")
    @Operation(summary = "Unban user", description = "Unban a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unbanUser(@PathVariable UUID userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User unbanned successfully")
                .build());
    }

    @PatchMapping("/{userId}/verify")
    @Operation(summary = "Verify user", description = "Verify a user account")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> verifyUser(@PathVariable UUID userId) {
        userService.verifyUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User verified successfully")
                .build());
    }

    // ==================== User Search & Filtering ====================
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by keyword")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> searchUsers(
            @RequestParam String keyword,
            Pageable pageable) {
        Page<UserDetailResponse> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Search completed successfully")
                .data(users)
                .build());
    }

    @GetMapping("/type/{userType}")
    @Operation(summary = "Get users by type", description = "Retrieve users by their type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> getUsersByType(
            @PathVariable User.UserType userType,
            Pageable pageable) {
        Page<UserDetailResponse> users = userService.getUsersByType(userType, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role", description = "Retrieve users by their role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> getUsersByRole(
            @PathVariable User.Role role,
            Pageable pageable) {
        Page<UserDetailResponse> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active users", description = "Retrieve all active users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> getActiveUsers(Pageable pageable) {
        Page<UserDetailResponse> users = userService.getActiveUsers(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Active users retrieved successfully")
                .data(users)
                .build());
    }

    @GetMapping("/banned")
    @Operation(summary = "Get banned users", description = "Retrieve all banned users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserDetailResponse>>> getBannedUsers(Pageable pageable) {
        Page<UserDetailResponse> users = userService.getBannedUsers(pageable);
        return ResponseEntity.ok(ApiResponse.<Page<UserDetailResponse>>builder()
                .success(true)
                .message("Banned users retrieved successfully")
                .data(users)
                .build());
    }

    // ==================== User Statistics ====================
    
    @GetMapping("/management/summary")
    @Operation(summary = "Get user management summary", description = "Get comprehensive user management statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<UserManagementResponse>> getUserManagementSummary(Pageable pageable) {
        UserManagementResponse summary = userService.getUserManagementSummary(pageable);
        return ResponseEntity.ok(ApiResponse.<UserManagementResponse>builder()
                .success(true)
                .message("User management summary retrieved successfully")
                .data(summary)
                .build());
    }

    @GetMapping("/{userId}/statistics")
    @Operation(summary = "Get user statistics", description = "Get detailed statistics for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserStatistics(@PathVariable UUID userId) {
        Map<String, Object> stats = userService.getUserStatistics(userId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("User statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/statistics/total")
    @Operation(summary = "Get total users count", description = "Get the total number of users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<Long>> getTotalUsers() {
        Long count = userService.getTotalUsers();
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Total users count retrieved successfully")
                .data(count)
                .build());
    }

    // ==================== Email & Phone Verification ====================
    
    @PostMapping("/{userId}/send-verification")
    @Operation(summary = "Send verification email", description = "Send email verification link to user")
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@PathVariable UUID userId) {
        userService.sendVerificationEmail(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Verification email sent successfully")
                .build());
    }

    @PostMapping("/{userId}/verify-email")
    @Operation(summary = "Verify email", description = "Verify user's email with token")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @PathVariable UUID userId,
            @RequestParam String token) {
        userService.verifyEmail(userId, token);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Email verified successfully")
                .build());
    }

    @PostMapping("/{userId}/verify-phone")
    @Operation(summary = "Verify phone", description = "Verify user's phone with code")
    public ResponseEntity<ApiResponse<Void>> verifyPhone(
            @PathVariable UUID userId,
            @RequestParam String code) {
        userService.verifyPhone(userId, code);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Phone verified successfully")
                .build());
    }

    // ==================== Password Management ====================
    
    @PostMapping("/{userId}/change-password")
    @Operation(summary = "Change password", description = "Change user's password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable UUID userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .build());
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Request password reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam String email) {
        userService.resetPassword(email);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset email sent successfully")
                .build());
    }

    @PostMapping("/reset-password/confirm")
    @Operation(summary = "Confirm password reset", description = "Reset password with token")
    public ResponseEntity<ApiResponse<Void>> updatePasswordWithToken(
            @RequestParam String token,
            @RequestParam String newPassword) {
        userService.updatePasswordWithToken(token, newPassword);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Password reset successfully")
                .build());
    }

    // ==================== Social Features ====================
    
    @PostMapping("/{followerId}/follow/{followedId}")
    @Operation(summary = "Follow user", description = "Follow another user")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable UUID followerId,
            @PathVariable UUID followedId) {
        userService.followUser(followerId, followedId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User followed successfully")
                .build());
    }

    @DeleteMapping("/{followerId}/unfollow/{followedId}")
    @Operation(summary = "Unfollow user", description = "Unfollow a user")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable UUID followerId,
            @PathVariable UUID followedId) {
        userService.unfollowUser(followerId, followedId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User unfollowed successfully")
                .build());
    }

    @GetMapping("/{userId}/followers/count")
    @Operation(summary = "Get followers count", description = "Get the number of followers for a user")
    public ResponseEntity<ApiResponse<Long>> getFollowersCount(@PathVariable UUID userId) {
        Long count = userService.getFollowersCount(userId);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Followers count retrieved successfully")
                .data(count)
                .build());
    }

    @GetMapping("/{userId}/following/count")
    @Operation(summary = "Get following count", description = "Get the number of users this user is following")
    public ResponseEntity<ApiResponse<Long>> getFollowingCount(@PathVariable UUID userId) {
        Long count = userService.getFollowingCount(userId);
        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .success(true)
                .message("Following count retrieved successfully")
                .data(count)
                .build());
    }

    // ==================== Score Management ====================
    
    @PatchMapping("/{userId}/update-trust-score")
    @Operation(summary = "Update trust score", description = "Recalculate and update user's trust score")
    public ResponseEntity<ApiResponse<Void>> updateTrustScore(@PathVariable UUID userId) {
        userService.updateTrustScore(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Trust score updated successfully")
                .build());
    }

    @PatchMapping("/{userId}/update-sustainability-score")
    @Operation(summary = "Update sustainability score", description = "Recalculate and update user's sustainability score")
    public ResponseEntity<ApiResponse<Void>> updateSustainabilityScore(@PathVariable UUID userId) {
        userService.updateSustainabilityScore(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Sustainability score updated successfully")
                .build());
    }
}









