package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.enums.UserType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private UserType userType;
    private Integer sustainabilityScore;
    private boolean emailVerified;
} 