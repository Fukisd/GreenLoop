package org.greenloop.circularfashion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "promotions", indexes = {
    @Index(name = "idx_promotion_code", columnList = "promotion_code"),
    @Index(name = "idx_promotion_type", columnList = "promotion_type"),
    @Index(name = "idx_promotion_status", columnList = "is_active"),
    @Index(name = "idx_promotion_dates", columnList = "start_date, end_date"),
    @Index(name = "idx_promotion_usage", columnList = "current_usage_count")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Promotion extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long promotionId;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "promotion_code", unique = true, length = 50)
    private String promotionCode;
    
    @Column(name = "promotion_type", length = 50, nullable = false)
    private String promotionType; // DISCOUNT, CASHBACK, POINTS_MULTIPLIER, FREE_SHIPPING, etc.
    
    // Timing
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Column(name = "is_active", nullable = false)
    @lombok.Builder.Default
    private Boolean isActive = true;
    
    // Discount details
    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT, POINTS
    
    @Column(name = "discount_value", precision = 10, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "max_discount_amount", precision = 10, scale = 2)
    private BigDecimal maxDiscountAmount;
    
    @Column(name = "min_purchase_amount", precision = 10, scale = 2)
    private BigDecimal minPurchaseAmount;
    
    // Usage limits
    @Column(name = "max_usage_count")
    private Long maxUsageCount;
    
    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;
    
    @Column(name = "current_usage_count")
    @lombok.Builder.Default
    private Long currentUsageCount = 0L;
    
    // Target criteria
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_criteria", columnDefinition = "jsonb")
    private Map<String, Object> targetCriteria; // user tiers, categories, brands, etc.
    
    // Applicable categories and brands
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_categories",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> applicableCategories;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "promotion_brands",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "brand_id")
    )
    private List<Brand> applicableBrands;
    
    // Points and rewards
    @Column(name = "points_reward", precision = 10, scale = 2)
    private BigDecimal pointsReward;
    
    @Column(name = "points_multiplier", precision = 3, scale = 2)
    private BigDecimal pointsMultiplier;
    
    // Display settings
    @Column(name = "banner_image_url")
    private String bannerImageUrl;
    
    @Column(name = "thumbnail_image_url")
    private String thumbnailImageUrl;
    
    @Column(name = "is_featured", nullable = false)
    @lombok.Builder.Default
    private Boolean isFeatured = false;
    
    @Column(name = "display_priority")
    @lombok.Builder.Default
    private Integer displayPriority = 0;
    
    // Terms and conditions
    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;
    
    // Analytics
    @Column(name = "view_count")
    @lombok.Builder.Default
    private Long viewCount = 0L;
    
    @Column(name = "click_count")
    @lombok.Builder.Default
    private Long clickCount = 0L;
    
    @Column(name = "conversion_count")
    @lombok.Builder.Default
    private Long conversionCount = 0L;
    
    @Column(name = "total_revenue_generated", precision = 12, scale = 2)
    @lombok.Builder.Default
    private BigDecimal totalRevenueGenerated = BigDecimal.ZERO;
    
    // Creator and approval
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_admin_id", nullable = false)
    private User createdByAdmin;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_admin_id")
    private User approvedByAdmin;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    // Additional settings
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_settings", columnDefinition = "jsonb")
    private Map<String, Object> additionalSettings;
    
    @Column(name = "auto_apply", nullable = false)
    @lombok.Builder.Default
    private Boolean autoApply = false; // Automatically apply if conditions met
} 


