package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointEarningRuleResponse {

    private UUID ruleId;
    private String ruleName;
    private String description;
    private Integer pointsPerPurchase;
    private Integer pointsPerCollection;
    private Integer pointsPerReview;
    private Integer pointsPerReferral;
    private Integer signupBonus;
    private Integer dailyLoginPoints;
    private Integer pointValueInCurrency;
    private Integer minimumRedemptionPoints;
    private Integer pointsExpireInDays;
    private Boolean expirationEnabled;
    private Double eventMultiplier;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;
    private Boolean isActive;
    private Boolean isEventActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}









