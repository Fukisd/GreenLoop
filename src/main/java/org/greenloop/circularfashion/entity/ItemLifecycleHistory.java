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
import java.util.Map;

@Entity
@Table(name = "item_lifecycle_history", indexes = {
    @Index(name = "idx_lifecycle_item", columnList = "item_id"),
    @Index(name = "idx_lifecycle_user", columnList = "user_id"),
    @Index(name = "idx_lifecycle_event_type", columnList = "event_type"),
    @Index(name = "idx_lifecycle_event_date", columnList = "event_date"),
    @Index(name = "idx_lifecycle_location", columnList = "location_id")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemLifecycleHistory extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // User involved in this event
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private CollectionPoint location; // Collection point if applicable
    
    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType; // CREATED, LISTED, SOLD, RENTED, COLLECTED, EVALUATED, RECYCLED, etc.
    
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Previous and new status
    @Column(name = "previous_status", length = 50)
    private String previousStatus;
    
    @Column(name = "new_status", length = 50)
    private String newStatus;
    
    // Location tracking
    @Column(name = "previous_location")
    private String previousLocation;
    
    @Column(name = "new_location")
    private String newLocation;
    
    // Owner tracking
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_owner_id")
    private User previousOwner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_owner_id")
    private User newOwner;
    
    // Transaction reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;
    
    // Financial impact
    @Column(name = "financial_value", precision = 10, scale = 2)
    private BigDecimal financialValue;
    
    @Column(name = "points_involved", precision = 10, scale = 2)
    private BigDecimal pointsInvolved;
    
    // Environmental impact metrics
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "environmental_impact", columnDefinition = "jsonb")
    private Map<String, Object> environmentalImpact; // CO2 saved, water saved, energy saved, etc.
    
    // Event metadata and additional data
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_metadata", columnDefinition = "jsonb")
    private Map<String, Object> eventMetadata;
    
    // Quality and condition tracking
    @Column(name = "condition_before", precision = 3, scale = 2)
    private BigDecimal conditionBefore;
    
    @Column(name = "condition_after", precision = 3, scale = 2)
    private BigDecimal conditionAfter;
    
    // System tracking
    @Column(name = "automated_event", nullable = false)
    @lombok.Builder.Default
    private Boolean automatedEvent = false; // True if triggered by IoT/AI
    
    @Column(name = "device_id", length = 100)
    private String deviceId; // IoT device that triggered this event
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
} 