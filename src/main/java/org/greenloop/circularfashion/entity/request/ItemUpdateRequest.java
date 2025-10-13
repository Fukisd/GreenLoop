package org.greenloop.circularfashion.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemUpdateRequest {
    private UUID categoryId;
    private UUID brandId;
    private String name;
    private String description;
    private String size;
    private String color;
    private BigDecimal conditionRating;
    private String conditionDescription;
    private LocalDate purchaseDate;
    private BigDecimal originalPrice;
    private String location;
    private List<String> images;
    private Map<String, Integer> materialComposition;
    private String careInstructions;
    private Map<String, Object> sustainabilityMetrics;
    private String rfidTag;
    private String qrCode;
    private String sku;
    
    // Additional fields for item management
    private Integer weightGrams;
    private Map<String, BigDecimal> dimensions;
    private BigDecimal conditionScore;
    private BigDecimal currentEstimatedValue;
    private String acquisitionMethod;
    private List<String> videos;
    private List<String> tags;
    private Map<String, Object> metadata;
    private BigDecimal carbonFootprintKg;
    private BigDecimal waterSavedLiters;
    private BigDecimal energySavedKwh;
    private String itemStatus;
} 