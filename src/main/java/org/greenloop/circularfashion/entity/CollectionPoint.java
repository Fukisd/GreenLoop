package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "collection_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class CollectionPoint {

    @Id
    @GeneratedValue
    @Column(name = "point_id")
    private UUID pointId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_type", nullable = false)
    private PointType pointType;

    // Location
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    @JsonIgnore
    private UserAddress address;

    // Capacity and status
    @Column(name = "max_capacity")
    @Builder.Default
    private Integer maxCapacity = 100;

    @Column(name = "current_capacity")
    @Builder.Default
    private Integer currentCapacity = 0;

    // Operating information
    @Type(JsonType.class)
    @Column(name = "operating_hours", columnDefinition = "jsonb")
    private Map<String, Map<String, String>> operatingHours; // {"monday": {"open": "08:00", "close": "18:00"}}

    @Type(JsonType.class)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, String> contactInfo;

    // IoT integration ready
    @Column(name = "iot_device_id")
    private String iotDeviceId;

    @Type(JsonType.class)
    @Column(name = "sensor_data", columnDefinition = "jsonb")
    private Map<String, Object> sensorData;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "collectionPoint", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CollectionRequest> collectionRequests = new HashSet<>();

    // Enums
    public enum PointType {
        RESIDENTIAL, PUBLIC, COMMERCIAL, PARTNER
    }

    // Helper methods
    public boolean isFull() {
        return currentCapacity != null && maxCapacity != null && 
               currentCapacity >= maxCapacity;
    }

    public boolean isAvailable() {
        return isActive && !isFull();
    }

    public int getAvailableCapacity() {
        if (maxCapacity == null || currentCapacity == null) {
            return 0;
        }
        return Math.max(0, maxCapacity - currentCapacity);
    }

    public double getCapacityPercentage() {
        if (maxCapacity == null || maxCapacity == 0 || currentCapacity == null) {
            return 0.0;
        }
        return (double) currentCapacity / maxCapacity * 100;
    }

    public void addCapacity(int items) {
        if (currentCapacity == null) {
            currentCapacity = 0;
        }
        currentCapacity += items;
    }

    public void removeCapacity(int items) {
        if (currentCapacity == null) {
            currentCapacity = 0;
        }
        currentCapacity = Math.max(0, currentCapacity - items);
    }

    @PrePersist
    protected void onCreate() {
        if (pointId == null) {
            pointId = UUID.randomUUID();
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
 