package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "collection_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CollectionRequest {

    @Id
    @GeneratedValue
    @Column(name = "request_id")
    private UUID requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Collection details
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_address_id", nullable = false)
    @JsonIgnore
    private UserAddress pickupAddress;

    @Column(name = "preferred_date")
    private LocalDate preferredDate;

    @Column(name = "preferred_time_slot")
    private String preferredTimeSlot;

    // Items to be collected
    @Column(name = "estimated_items_count")
    private Integer estimatedItemsCount;

    @Type(JsonType.class)
    @Column(name = "item_categories", columnDefinition = "jsonb")
    private List<String> itemCategories; // Categories of items to collect

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    // Request status
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.PENDING;

    @Column(name = "priority_level")
    @Builder.Default
    private Integer priorityLevel = 3; // 1-5, 5 is highest

    // Assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_collector_id")
    @JsonIgnore
    private User assignedCollector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_point_id")
    @JsonIgnore
    private CollectionPoint collectionPoint;

    // Completion
    @Column(name = "collected_at")
    private LocalDateTime collectedAt;

    @Column(name = "collector_notes", columnDefinition = "TEXT")
    private String collectorNotes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "collectionRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CollectionRequestItem> requestItems = new HashSet<>();

    // Enums
    public enum Status {
        PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    // Helper methods
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }

    public boolean isAssigned() {
        return assignedCollector != null;
    }

    public boolean isHighPriority() {
        return priorityLevel != null && priorityLevel >= 4;
    }

    public boolean canBeScheduled() {
        return status == Status.PENDING && preferredDate != null;
    }

    public void assignCollector(User collector) {
        this.assignedCollector = collector;
        if (status == Status.PENDING) {
            this.status = Status.SCHEDULED;
        }
    }

    public void markAsCompleted(String notes) {
        this.status = Status.COMPLETED;
        this.collectedAt = LocalDateTime.now();
        this.collectorNotes = notes;
    }

    @PrePersist
    protected void onCreate() {
        if (requestId == null) {
            requestId = UUID.randomUUID();
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
