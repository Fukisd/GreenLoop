package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetailResponse {

    private UUID userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String userType;
    private String role;
    private String avatarUrl;
    private String bio;
    
    // Points and scores
    private Integer sustainabilityPoints;
    private BigDecimal sustainabilityScore;
    private BigDecimal trustScore;
    
    // Account status
    private Boolean isVerified;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Boolean isActive;
    private Boolean isBanned;
    
    // Statistics
    private Long followersCount;
    private Long followingCount;
    private Long itemsCount;
    private Long listingsCount;
    private Long ordersCount;
    
    // Timestamps
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}









