package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.entity.Item;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {
    
    private UUID itemId;
    private String itemCode;
    
    // Basic information
    private String name;
    private String description;
    private String displayName;
    
    // Category and Brand
    private UUID categoryId;
    private String categoryName;
    private String categorySlug;
    
    private UUID brandId;
    private String brandName;
    private String brandLogoUrl;
    
    // Physical properties
    private String size;
    private String color;
    private Map<String, Integer> materialComposition;
    private Integer weightGrams;
    private Map<String, BigDecimal> dimensions;
    
    // Condition and valuation
    private BigDecimal conditionScore;
    private String conditionText;
    private String conditionDescription;
    private BigDecimal originalPrice;
    private BigDecimal currentEstimatedValue;
    
    // Ownership
    private UUID originalOwnerId;
    private String originalOwnerName;
    private UUID currentOwnerId;
    private String currentOwnerName;
    private Item.AcquisitionMethod acquisitionMethod;
    
    // Status
    private Item.ItemStatus itemStatus;
    private Boolean isVerified;
    private LocalDateTime verificationDate;
    private UUID verifiedById;
    private String verifiedByName;
    
    // Sustainability metrics
    private BigDecimal carbonFootprintKg;
    private BigDecimal waterSavedLiters;
    private BigDecimal energySavedKwh;
    private Boolean isSustainable;
    
    // Media
    private List<String> images;
    private List<String> videos;
    private String primaryImageUrl;
    
    // Metadata
    private List<String> tags;
    private Map<String, Object> metadata;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed flags
    private Boolean availableForListing;
    private Boolean inMarketplace;
    private Boolean hasImages;
    private Boolean hasVideos;
}










