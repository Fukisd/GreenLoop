package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MarketplaceListingService {
    MarketplaceListing create(MarketplaceListing listing);
    Optional<MarketplaceListing> getById(UUID id); // Changed from Long to UUID
    List<MarketplaceListing> getBySeller(UUID sellerId); // Changed from Long to UUID
    List<MarketplaceListing> getByStatus(ListingStatus status);
    List<MarketplaceListing> getByType(ListingType type);
    List<MarketplaceListing> getByStatusAndType(ListingStatus status, ListingType type);
    MarketplaceListing update(UUID id, MarketplaceListing updated); // Changed from Long to UUID
    void delete(UUID id); // Changed from Long to UUID
} 