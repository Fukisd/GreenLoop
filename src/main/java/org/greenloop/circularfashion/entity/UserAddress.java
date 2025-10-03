package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class UserAddress {

    @Id
    @GeneratedValue
    @Column(name = "address_id")
    private UUID addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType;

    @Column(name = "label")
    private String label;

    // Address details (Vietnam-focused)
    @Column(name = "street_address", nullable = false, columnDefinition = "TEXT")
    private String streetAddress;

    @Column(name = "ward")
    private String ward;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "country")
    @Builder.Default
    private String country = "Vietnam";

    // Coordinates for collection routing
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "is_collection_point")
    @Builder.Default
    private Boolean isCollectionPoint = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum AddressType {
        HOME, WORK, COLLECTION_POINT, OTHER
    }

    // Helper methods
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        
        if (streetAddress != null) {
            address.append(streetAddress);
        }
        
        if (ward != null) {
            if (address.length() > 0) address.append(", ");
            address.append(ward);
        }
        
        if (district != null) {
            if (address.length() > 0) address.append(", ");
            address.append(district);
        }
        
        if (city != null) {
            if (address.length() > 0) address.append(", ");
            address.append(city);
        }
        
        if (province != null) {
            if (address.length() > 0) address.append(", ");
            address.append(province);
        }
        
        return address.toString();
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }

    public String getDisplayName() {
        if (label != null && !label.trim().isEmpty()) {
            return label;
        }
        return addressType.name().toLowerCase().replace("_", " ");
    }

    @PrePersist
    protected void onCreate() {
        if (addressId == null) {
            addressId = UUID.randomUUID();
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
