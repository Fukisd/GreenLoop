package org.greenloop.circularfashion.entity.response;

import org.greenloop.circularfashion.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserType userType;
    private Integer sustainabilityScore;
    private boolean emailVerified;
    private String avatarUrl;
    private LocalDateTime createdAt;
} 