package org.greenloop.circularfashion.entity.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandUpdateRequest {
    
    @Size(min = 2, max = 100, message = "Brand name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Pattern(regexp = "^https?://.*", message = "Logo URL must be a valid URL")
    private String logoUrl;
    
    @Pattern(regexp = "^https?://.*", message = "Website must be a valid URL")
    private String website;
    
    @DecimalMin(value = "0.0", message = "Sustainability rating must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Sustainability rating must be at most 5.0")
    private BigDecimal sustainabilityRating;
    
    private Map<String, Object> ecoCertification;
    
    private Boolean isVerified;
    
    private Boolean isPartner;
    
    private Boolean isActive;
}









