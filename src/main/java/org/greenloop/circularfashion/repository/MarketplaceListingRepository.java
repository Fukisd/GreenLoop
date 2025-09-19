package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, Long> {
    
    List<MarketplaceListing> findBySellerUserId(Long sellerId);
    
    List<MarketplaceListing> findByStatus(ListingStatus status);
    
    List<MarketplaceListing> findByListingType(ListingType listingType);
    
    List<MarketplaceListing> findByStatusAndListingType(ListingStatus status, ListingType listingType);
} 