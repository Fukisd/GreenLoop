package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "point_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class PointTransaction {

    @Id
    @GeneratedValue
    @Column(name = "transaction_id")
    private UUID transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Transaction details
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "points_amount", nullable = false)
    private Integer pointsAmount;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Related entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_request_id")
    @JsonIgnore
    private CollectionRequest collectionRequest;

    // Balance tracking
    @Column(name = "balance_before", nullable = false)
    private Integer balanceBefore;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    // Expiration (for earned points)
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.COMPLETED;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum TransactionType {
        EARNED_COLLECTION, EARNED_PURCHASE, EARNED_REVIEW, EARNED_REFERRAL,
        SPENT_DISCOUNT, SPENT_PREMIUM, EXPIRED, ADJUSTMENT
    }

    public enum Status {
        PENDING, COMPLETED, CANCELLED, EXPIRED
    }

    // Helper methods
    public boolean isEarned() {
        return transactionType.name().startsWith("EARNED");
    }

    public boolean isSpent() {
        return transactionType.name().startsWith("SPENT");
    }

    public boolean isExpired() {
        return status == Status.EXPIRED || 
               (expiresAt != null && expiresAt.isBefore(LocalDateTime.now()));
    }

    public boolean isActive() {
        return status == Status.COMPLETED && !isExpired();
    }

    @PrePersist
    protected void onCreate() {
        if (transactionId == null) {
            transactionId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 
 