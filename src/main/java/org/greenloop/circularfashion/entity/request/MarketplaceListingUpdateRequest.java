package org.greenloop.circularfashion.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceListingUpdateRequest {
    private ListingType listingType;
    private BigDecimal price;
    private BigDecimal rentalPricePerDay;
    private String title;
    private String description;
    private List<String> tags;
    private ListingStatus status;
    private LocalDateTime expiresAt;
} 