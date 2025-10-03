package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private String userId; // Changed from UUID to String
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String phone; // Added phone field
    private User.UserType userType;
    private User.Role role;
    private Double sustainabilityScore;
    private Integer sustainabilityPoints;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private String avatarUrl;
    private String createdAt;
    private String updatedAt;
}
