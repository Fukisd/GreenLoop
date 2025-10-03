package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private UUID itemId;

    @Column(name = "item_code", unique = true, nullable = false)
    private String itemCode; // QR/Barcode for tracking

    // Basic information
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    @JsonIgnore
    private Brand brand;

    // Physical properties
    @Column(name = "size")
    private String size;

    @Column(name = "color")
    private String color;

    @Type(JsonType.class)
    @Column(name = "material_composition", columnDefinition = "jsonb")
    private Map<String, Integer> materialComposition; // {"cotton": 70, "polyester": 30}

    @Column(name = "weight_grams")
    private Integer weightGrams;

    @Type(JsonType.class)
    @Column(name = "dimensions", columnDefinition = "jsonb")
    private Map<String, BigDecimal> dimensions; // {"length": 50, "width": 40, "height": 2}

    // Condition and valuation
    @Column(name = "condition_score", precision = 3, scale = 2, nullable = false)
    private BigDecimal conditionScore; // 1.0 to 5.0

    @Column(name = "condition_description", columnDefinition = "TEXT")
    private String conditionDescription;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "current_estimated_value", precision = 12, scale = 2)
    private BigDecimal currentEstimatedValue;

    // Ownership and lifecycle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_owner_id")
    @JsonIgnore
    private User originalOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_owner_id")
    @JsonIgnore
    private User currentOwner;

    @Enumerated(EnumType.STRING)
    @Column(name = "acquisition_method", nullable = false)
    private AcquisitionMethod acquisitionMethod;

    // Item status in system
    @Enumerated(EnumType.STRING)
    @Column(name = "item_status", nullable = false)
    @Builder.Default
    private ItemStatus itemStatus = ItemStatus.SUBMITTED;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    @JsonIgnore
    private User verifiedBy;

    // Sustainability data
    @Column(name = "carbon_footprint_kg", precision = 10, scale = 4)
    private BigDecimal carbonFootprintKg;

    @Column(name = "water_saved_liters", precision = 10, scale = 2)
    private BigDecimal waterSavedLiters;

    @Column(name = "energy_saved_kwh", precision = 10, scale = 2)
    private BigDecimal energySavedKwh;

    // Media
    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images; // Array of image URLs

    @Type(JsonType.class)
    @Column(name = "videos", columnDefinition = "jsonb")
    private List<String> videos; // Array of video URLs

    // Metadata
    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags; // Searchable tags

    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata; // Additional flexible data

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<ItemLifecycle> lifecycleHistory = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MarketplaceListing> listings = new HashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviews = new HashSet<>();

    // Enums
    public enum ItemStatus {
        SUBMITTED, PENDING_COLLECTION, COLLECTED, VALUING, VALUED,
        PROCESSING, READY_FOR_SALE, LISTED, SOLD, RENTED, DONATED,
        RECYCLED, REJECTED
    }

    public enum AcquisitionMethod {
        COLLECTED, PURCHASED, TRADED, DONATED, IMPORTED
    }

    // Helper methods
    public boolean isAvailableForListing() {
        return itemStatus == ItemStatus.READY_FOR_SALE || itemStatus == ItemStatus.VALUED;
    }

    public boolean isInMarketplace() {
        return itemStatus == ItemStatus.LISTED;
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    public boolean hasVideos() {
        return videos != null && !videos.isEmpty();
    }

    public String getPrimaryImageUrl() {
        if (hasImages()) {
            return images.get(0);
        }
        return null;
    }

    public String getConditionText() {
        if (conditionScore == null) return "Unknown";
        
        BigDecimal score = conditionScore;
        if (score.compareTo(BigDecimal.valueOf(4.5)) >= 0) return "Excellent";
        if (score.compareTo(BigDecimal.valueOf(3.5)) >= 0) return "Very Good";
        if (score.compareTo(BigDecimal.valueOf(2.5)) >= 0) return "Good";
        if (score.compareTo(BigDecimal.valueOf(1.5)) >= 0) return "Fair";
        return "Poor";
    }

    public boolean isSustainable() {
        return carbonFootprintKg != null || waterSavedLiters != null || energySavedKwh != null;
    }

    public String getDisplayName() {
        StringBuilder displayName = new StringBuilder();
        
        if (brand != null && brand.getName() != null) {
            displayName.append(brand.getName()).append(" ");
        }
        
        displayName.append(name);
        
        if (size != null) {
            displayName.append(" (").append(size).append(")");
        }
        
        return displayName.toString();
    }

    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) return;
        
        if (tags == null) {
            tags = new ArrayList<>();
        }
        
        String normalizedTag = tag.toLowerCase().trim();
        if (!tags.contains(normalizedTag)) {
            tags.add(normalizedTag);
        }
    }

    public void removeTag(String tag) {
        if (tags != null && tag != null) {
            tags.remove(tag.toLowerCase().trim());
        }
    }

    public void updateStatus(ItemStatus newStatus, User changedBy, String reason) {
        ItemStatus oldStatus = this.itemStatus;
        this.itemStatus = newStatus;
        
        // Create lifecycle entry
        ItemLifecycle lifecycle = ItemLifecycle.builder()
                .item(this)
                .previousStatus(oldStatus != null ? oldStatus.name() : null)
                .newStatus(newStatus.name())
                .changeReason(reason)
                .changedBy(changedBy)
                .createdAt(LocalDateTime.now())
                .build();
        
        this.lifecycleHistory.add(lifecycle);
    }

    @PrePersist
    protected void onCreate() {
        if (itemId == null) {
            itemId = UUID.randomUUID();
        }
        if (itemCode == null) {
            itemCode = generateItemCode();
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

    private String generateItemCode() {
        // Generate a unique item code: GL + timestamp + random
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 1000));
        return "GL" + timestamp.substring(timestamp.length() - 8) + random;
    }
} 