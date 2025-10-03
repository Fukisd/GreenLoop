package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_lifecycle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ItemLifecycle {

    @Id
    @GeneratedValue
    @Column(name = "lifecycle_id")
    private UUID lifecycleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private Item item;

    @Column(name = "previous_status")
    private String previousStatus;

    @Column(name = "new_status", nullable = false)
    private String newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_owner_id")
    @JsonIgnore
    private User previousOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_owner_id")
    @JsonIgnore
    private User newOwner;

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Environmental impact of this change
    @Column(name = "carbon_impact_kg", precision = 10, scale = 4)
    private BigDecimal carbonImpactKg;

    @Column(name = "energy_impact_kwh", precision = 10, scale = 2)
    private BigDecimal energyImpactKwh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    @JsonIgnore
    private User changedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public boolean isStatusChange() {
        return !newStatus.equals(previousStatus);
    }

    public boolean isOwnershipChange() {
        return newOwner != null && !newOwner.equals(previousOwner);
    }

    public boolean hasEnvironmentalImpact() {
        return carbonImpactKg != null || energyImpactKwh != null;
    }

    @PrePersist
    protected void onCreate() {
        if (lifecycleId == null) {
            lifecycleId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
} 