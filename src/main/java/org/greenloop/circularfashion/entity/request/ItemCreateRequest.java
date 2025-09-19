package org.greenloop.circularfashion.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCreateRequest {
    @NotNull
    private Long ownerId;
    private Long categoryId;
    private Long brandId;
    @NotBlank
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
} 