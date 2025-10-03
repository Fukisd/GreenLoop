package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "collection_request_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CollectionRequestItem {

    @Id
    @GeneratedValue
    @Column(name = "request_item_id")
    private UUID requestItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_request_id", nullable = false)
    @JsonIgnore
    private CollectionRequest collectionRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item; // Linked after valuation

    // User-provided information
    @Column(name = "user_description", columnDefinition = "TEXT")
    private String userDescription;

    @Column(name = "user_estimated_value", precision = 10, scale = 2)
    private BigDecimal userEstimatedValue;

    @Type(JsonType.class)
    @Column(name = "user_images", columnDefinition = "jsonb")
    private List<String> userImages;

    // Valuation results
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "valuator_id")
    @JsonIgnore
    private User valuator;

    @Column(name = "final_valuation", precision = 10, scale = 2)
    private BigDecimal finalValuation;

    @Column(name = "valuation_notes", columnDefinition = "TEXT")
    private String valuationNotes;

    @Column(name = "valuation_date")
    private LocalDateTime valuationDate;

    // Points awarded
    @Column(name = "points_awarded")
    @Builder.Default
    private Integer pointsAwarded = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Enums
    public enum Status {
        PENDING, ACCEPTED, REJECTED, PROCESSING
    }

    // Helper methods
    public boolean isValuated() {
        return finalValuation != null && valuationDate != null;
    }

    public boolean isAccepted() {
        return status == Status.ACCEPTED;
    }

    public boolean hasUserImages() {
        return userImages != null && !userImages.isEmpty();
    }

    public void valuate(User valuator, BigDecimal valuation, String notes, Integer points) {
        this.valuator = valuator;
        this.finalValuation = valuation;
        this.valuationNotes = notes;
        this.valuationDate = LocalDateTime.now();
        this.pointsAwarded = points != null ? points : 0;
        this.status = Status.ACCEPTED;
    }

    public void reject(String reason) {
        this.valuationNotes = reason;
        this.valuationDate = LocalDateTime.now();
        this.status = Status.REJECTED;
    }

    @PrePersist
    protected void onCreate() {
        if (requestItemId == null) {
            requestItemId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 
 