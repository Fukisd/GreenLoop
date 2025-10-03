package org.greenloop.circularfashion.mapper;

import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .userId(user.getUserId().toString()) // Convert UUID to String
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
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
    }

    public UserResponse toUserResponseWithDetails(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .userId(user.getUserId().toString()) // Convert UUID to String
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
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
    }
} 