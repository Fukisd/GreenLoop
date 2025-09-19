package org.greenloop.circularfashion.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.greenloop.circularfashion.enums.ListingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceListingCreateRequest {
    @NotNull
    private Long itemId;
    @NotNull
    private Long sellerId;
    @NotNull
    private ListingType listingType;
    private BigDecimal price;
    private BigDecimal rentalPricePerDay;
    @NotBlank
    private String title;
    private String description;
    private List<String> tags;
    private LocalDateTime expiresAt;
} 