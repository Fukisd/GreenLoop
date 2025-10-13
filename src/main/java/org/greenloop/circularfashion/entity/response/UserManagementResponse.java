package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserManagementResponse {

    private Page<UserDetailResponse> users;
    private Long totalUsers;
    private Long activeUsers;
    private Long bannedUsers;
    private Long verifiedUsers;
    private Map<String, Long> usersByType;
    private Map<String, Long> usersByRole;
}









