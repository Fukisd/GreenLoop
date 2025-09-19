package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;

import java.util.List;
import java.util.Optional;

public interface MarketplaceListingService {
    MarketplaceListing create(MarketplaceListing listing);
    Optional<MarketplaceListing> getById(Long id);
    List<MarketplaceListing> getBySeller(Long sellerId);
    List<MarketplaceListing> getByStatus(ListingStatus status);
    List<MarketplaceListing> getByType(ListingType type);
    List<MarketplaceListing> getByStatusAndType(ListingStatus status, ListingType type);
    MarketplaceListing update(Long id, MarketplaceListing updated);
    void delete(Long id);
} 