package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Review {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private UUID reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    @JsonIgnore
    private User reviewer;

    // Review target (one of these will be set)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id")
    @JsonIgnore
    private User reviewedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    // Review content
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1-5

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // Review categories (for detailed feedback)
    @Column(name = "quality_rating")
    private Integer qualityRating; // 1-5

    @Column(name = "communication_rating")
    private Integer communicationRating; // 1-5

    @Column(name = "delivery_rating")
    private Integer deliveryRating; // 1-5

    @Column(name = "value_rating")
    private Integer valueRating; // 1-5

    // Media
    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;

    @Type(JsonType.class)
    @Column(name = "videos", columnDefinition = "jsonb")
    private List<String> videos;

    // Status
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    // Helpful votes
    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Column(name = "not_helpful_count")
    @Builder.Default
    private Integer notHelpfulCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public boolean isPositive() {
        return rating != null && rating >= 4;
    }

    public boolean isNegative() {
        return rating != null && rating <= 2;
    }

    public boolean hasMedia() {
        return (images != null && !images.isEmpty()) || (videos != null && !videos.isEmpty());
    }

    public boolean isVisible() {
        return !isHidden;
    }

    public Double getAverageSubRating() {
        int count = 0;
        int total = 0;

        if (qualityRating != null) {
            total += qualityRating;
            count++;
        }
        if (communicationRating != null) {
            total += communicationRating;
            count++;
        }
        if (deliveryRating != null) {
            total += deliveryRating;
            count++;
        }
        if (valueRating != null) {
            total += valueRating;
            count++;
        }

        return count > 0 ? (double) total / count : null;
    }

    public void markAsHelpful() {
        this.helpfulCount = (this.helpfulCount != null ? this.helpfulCount : 0) + 1;
    }

    public void markAsNotHelpful() {
        this.notHelpfulCount = (this.notHelpfulCount != null ? this.notHelpfulCount : 0) + 1;
    }

    @PrePersist
    protected void onCreate() {
        if (reviewId == null) {
            reviewId = UUID.randomUUID();
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


