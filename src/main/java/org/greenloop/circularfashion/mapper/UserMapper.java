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
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .username(user.getUsername())
                .userType(user.getUserType())
                .sustainabilityScore(user.getSustainabilityScore())
                .emailVerified(user.isEmailVerified())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    public UserResponse toPublicUserResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .userType(user.getUserType())
                .sustainabilityScore(user.getSustainabilityScore())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
} 