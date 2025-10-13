package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandResponse {
    
    private UUID brandId;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private String website;
    
    // Sustainability metrics
    private BigDecimal sustainabilityRating;
    private Map<String, Object> ecoCertification;
    
    // Status
    private Boolean isVerified;
    private Boolean isPartner;
    private Boolean isActive;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private Boolean isSustainable;
    private Boolean hasEcoCertification;
    
    // Statistics
    private Long totalItems;
}









