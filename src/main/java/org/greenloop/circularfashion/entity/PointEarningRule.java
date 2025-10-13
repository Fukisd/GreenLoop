package org.greenloop.circularfashion.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "point_earning_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PointEarningRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rule_id")
    private UUID ruleId;

    @NotNull(message = "Rule name is required")
    @Column(name = "rule_name", unique = true, nullable = false)
    private String ruleName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Point earning configurations
    @Min(value = 1, message = "Points per purchase must be at least 1")
    @Column(name = "points_per_purchase")
    @Builder.Default
    private Integer pointsPerPurchase = 10; // Points per $1 spent

    @Min(value = 1, message = "Points per collection must be at least 1")
    @Column(name = "points_per_collection")
    @Builder.Default
    private Integer pointsPerCollection = 50; // Points for recycling items

    @Min(value = 1, message = "Points per review must be at least 1")
    @Column(name = "points_per_review")
    @Builder.Default
    private Integer pointsPerReview = 20; // Points for writing reviews

    @Min(value = 1, message = "Points per referral must be at least 1")
    @Column(name = "points_per_referral")
    @Builder.Default
    private Integer pointsPerReferral = 100; // Points for referring new users

    @Min(value = 1, message = "Sign up bonus must be at least 1")
    @Column(name = "signup_bonus")
    @Builder.Default
    private Integer signupBonus = 50; // Bonus points for new users

    @Min(value = 1, message = "Daily login points must be at least 1")
    @Column(name = "daily_login_points")
    @Builder.Default
    private Integer dailyLoginPoints = 5; // Points for daily login

    // Point redemption configurations
    @Min(value = 1, message = "Point value must be at least 1")
    @Column(name = "point_value_in_currency")
    @Builder.Default
    private Integer pointValueInCurrency = 100; // 1 point = 100 VND

    @Min(value = 1, message = "Minimum redemption must be at least 1")
    @Column(name = "minimum_redemption_points")
    @Builder.Default
    private Integer minimumRedemptionPoints = 100; // Minimum points to redeem

    // Point expiration
    @Column(name = "points_expire_in_days")
    @Builder.Default
    private Integer pointsExpireInDays = 365; // Points expire after 1 year

    @Column(name = "expiration_enabled")
    @Builder.Default
    private Boolean expirationEnabled = true;

    // Multipliers for special events
    @Column(name = "event_multiplier")
    @Builder.Default
    private Double eventMultiplier = 1.0; // Default no multiplier

    @Column(name = "event_start_date")
    private LocalDateTime eventStartDate;

    @Column(name = "event_end_date")
    private LocalDateTime eventEndDate;

    // Status
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isEventActive() {
        LocalDateTime now = LocalDateTime.now();
        return eventMultiplier != null && eventMultiplier > 1.0 
               && eventStartDate != null && eventEndDate != null
               && now.isAfter(eventStartDate) && now.isBefore(eventEndDate);
    }

    public Integer calculatePointsForPurchase(Double amount) {
        Integer basePoints = (int) (amount * pointsPerPurchase);
        if (isEventActive()) {
            return (int) (basePoints * eventMultiplier);
        }
        return basePoints;
    }

    public Integer calculatePointsForAction(String actionType) {
        Integer basePoints = switch (actionType) {
            case "COLLECTION" -> pointsPerCollection;
            case "REVIEW" -> pointsPerReview;
            case "REFERRAL" -> pointsPerReferral;
            case "SIGNUP" -> signupBonus;
            case "DAILY_LOGIN" -> dailyLoginPoints;
            default -> 0;
        };
        
        if (isEventActive()) {
            return (int) (basePoints * eventMultiplier);
        }
        return basePoints;
    }

    public LocalDateTime calculateExpirationDate() {
        if (expirationEnabled && pointsExpireInDays != null && pointsExpireInDays > 0) {
            return LocalDateTime.now().plusDays(pointsExpireInDays);
        }
        return null; // No expiration
    }

    @PrePersist
    protected void onCreate() {
        if (ruleId == null) {
            ruleId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}









