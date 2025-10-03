package org.greenloop.circularfashion.service.impl;

import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;
import org.greenloop.circularfashion.repository.MarketplaceListingRepository;
import org.greenloop.circularfashion.service.MarketplaceListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MarketplaceListingServiceImpl implements MarketplaceListingService {

    @Autowired
    private MarketplaceListingRepository marketplaceListingRepository;

    @Override
    public MarketplaceListing create(MarketplaceListing listing) {
        return marketplaceListingRepository.save(listing);
    }

    @Override
    public Optional<MarketplaceListing> getById(UUID id) { // Changed from Long to UUID
        return marketplaceListingRepository.findById(id);
    }

    @Override
    public List<MarketplaceListing> getBySeller(UUID sellerId) { // Changed from Long to UUID
        return marketplaceListingRepository.findBySellerUserId(sellerId);
    }

    @Override
    public List<MarketplaceListing> getByStatus(ListingStatus status) {
        return marketplaceListingRepository.findByStatus(status);
    }

    @Override
    public List<MarketplaceListing> getByType(ListingType type) {
        return marketplaceListingRepository.findByListingType(type);
    }

    @Override
    public List<MarketplaceListing> getByStatusAndType(ListingStatus status, ListingType type) {
        return marketplaceListingRepository.findByStatusAndListingType(status, type);
    }

    @Override
    public MarketplaceListing update(UUID id, MarketplaceListing updated) { // Changed from Long to UUID
        return marketplaceListingRepository.findById(id)
                .map(existing -> {
                    updated.setListingId(existing.getListingId());
                    return marketplaceListingRepository.save(updated);
                })
                .orElseThrow(() -> new IllegalArgumentException("Listing not found with id: " + id));
    }

    @Override
    public void delete(UUID id) { // Changed from Long to UUID
        marketplaceListingRepository.deleteById(id);
    }
} 