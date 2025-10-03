package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "marketplace_listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MarketplaceListing {

    @Id
    @GeneratedValue
    @Column(name = "listing_id")
    private UUID listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @JsonIgnore
    private User seller;

    // Listing details
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType;

    // Pricing
    @Column(name = "price", precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "rental_price_per_day", precision = 10, scale = 2)
    private BigDecimal rentalPricePerDay;

    @Column(name = "rental_price_per_week", precision = 10, scale = 2)
    private BigDecimal rentalPricePerWeek;

    @Column(name = "rental_price_per_month", precision = 10, scale = 2)
    private BigDecimal rentalPricePerMonth;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    // Trading
    @Column(name = "accepts_trades")
    @Builder.Default
    private Boolean acceptsTrades = false;

    @Column(name = "preferred_trade_items", columnDefinition = "TEXT")
    private String preferredTradeItems;

    // Availability
    @Column(name = "available_from")
    @Builder.Default
    private LocalDate availableFrom = LocalDate.now();

    @Column(name = "available_until")
    private LocalDate availableUntil;

    @Column(name = "quantity_available")
    @Builder.Default
    private Integer quantityAvailable = 1;

    @Column(name = "min_rental_days")
    @Builder.Default
    private Integer minRentalDays = 1;

    @Column(name = "max_rental_days")
    @Builder.Default
    private Integer maxRentalDays = 30;

    // Location and delivery
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_location_id")
    @JsonIgnore
    private UserAddress pickupLocation;

    @Column(name = "delivery_available")
    @Builder.Default
    private Boolean deliveryAvailable = false;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "delivery_radius_km")
    private Integer deliveryRadiusKm;

    // Status and visibility
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private Status status = Status.DRAFT;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "boost_expires_at")
    private LocalDateTime boostExpiresAt;

    // Metrics
    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "favorite_count")
    @Builder.Default
    private Integer favoriteCount = 0;

    @Column(name = "inquiry_count")
    @Builder.Default
    private Integer inquiryCount = 0;

    // SEO and searchability
    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;

    @Column(name = "keywords", length = 500)
    private String keywords;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> orders = new HashSet<>();

    // Enums
    public enum ListingType {
        SELL, RENT, TRADE, FREE
    }

    public enum Status {
        DRAFT, ACTIVE, PAUSED, SOLD, RENTED, EXPIRED, REMOVED
    }

    // Helper methods
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean isAvailable() {
        return isActive() && (availableUntil == null || availableUntil.isAfter(LocalDate.now()));
    }

    public boolean isSold() {
        return status == Status.SOLD || status == Status.RENTED;
    }

    public boolean hasDelivery() {
        return deliveryAvailable != null && deliveryAvailable;
    }

    public BigDecimal getEffectivePrice() {
        return switch (listingType) {
            case SELL -> price;
            case RENT -> rentalPricePerDay;
            case TRADE, FREE -> BigDecimal.ZERO;
        };
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount != null ? this.viewCount : 0) + 1;
    }

    public void incrementFavoriteCount() {
        this.favoriteCount = (this.favoriteCount != null ? this.favoriteCount : 0) + 1;
    }

    public void incrementInquiryCount() {
        this.inquiryCount = (this.inquiryCount != null ? this.inquiryCount : 0) + 1;
    }

    public void activate() {
        if (status == Status.DRAFT) {
            this.status = Status.ACTIVE;
        }
    }

    public void markAsSold() {
        this.status = Status.SOLD;
    }

    public void markAsRented() {
        this.status = Status.RENTED;
    }

    @PrePersist
    protected void onCreate() {
        if (listingId == null) {
            listingId = UUID.randomUUID();
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