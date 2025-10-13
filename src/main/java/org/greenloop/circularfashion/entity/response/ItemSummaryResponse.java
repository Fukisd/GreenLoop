package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.entity.Item;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Lightweight Item response for list views
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemSummaryResponse {
    
    private UUID itemId;
    private String itemCode;
    private String name;
    private String displayName;
    
    // Category and Brand basics
    private UUID categoryId;
    private String categoryName;
    private UUID brandId;
    private String brandName;
    
    // Essential physical properties
    private String size;
    private String color;
    
    // Condition and price
    private BigDecimal conditionScore;
    private String conditionText;
    private BigDecimal currentEstimatedValue;
    
    // Status
    private Item.ItemStatus itemStatus;
    private Boolean isVerified;
    
    // Primary image only
    private String primaryImageUrl;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}










