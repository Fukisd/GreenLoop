package org.greenloop.circularfashion.entity;

import org.greenloop.circularfashion.enums.ItemStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_holder_id")
    private User currentHolder;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 20)
    private String size;
    
    @Column(length = 50)
    private String color;
    
    @Column(name = "condition_rating", precision = 3, scale = 2)
    private BigDecimal conditionRating;
    
    @Column(name = "condition_description", columnDefinition = "TEXT")
    private String conditionDescription;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "original_price", precision = 10, scale = 2)
    private BigDecimal originalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "current_status")
    @Builder.Default
    private ItemStatus currentStatus = ItemStatus.OWNED;
    
    private String location;
    
    // JSON column for storing array of image URLs
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;
    
    // JSON column for material composition (e.g., {"cotton": 80, "polyester": 20})
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "material_composition", columnDefinition = "jsonb")
    private Map<String, Integer> materialComposition;
    
    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;
    
    // JSON column for sustainability metrics
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sustainability_metrics", columnDefinition = "jsonb")
    private Map<String, Object> sustainabilityMetrics;
    
    @Column(name = "rfid_tag", length = 100)
    private String rfidTag;
    
    @Column(name = "qr_code", length = 100)
    private String qrCode;
    
    @Column(length = 100)
    private String sku;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 