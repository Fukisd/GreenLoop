package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Marketplace", description = "APIs for marketplace listings")
public class MarketplaceListingController {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @PostMapping("/listings")
    @Operation(summary = "Create listing", description = "Create a new marketplace listing")
    public ResponseEntity<MarketplaceListing> createListing(@RequestBody MarketplaceListing listing) {
        // Validate seller exists
        Optional<User> seller = userRepository.findById(listing.getSeller().getUserId());
        if (seller.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Validate item exists
        Optional<Item> item = itemRepository.findById(listing.getItem().getItemId());
        if (item.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Set the seller and item
        listing.setSeller(seller.get());
        listing.setItem(item.get());

        // Save the listing (you'll need to implement this service)
        // MarketplaceListing savedListing = marketplaceListingService.create(listing);
        
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/listings")
    @Operation(summary = "Get all listings", description = "Get all marketplace listings with pagination")
    public ResponseEntity<Page<MarketplaceListing>> getAllListings(Pageable pageable) {
        // This would need to be implemented in MarketplaceListingService
        return ResponseEntity.ok(Page.empty());
    }

    @GetMapping("/listings/{id}")
    @Operation(summary = "Get listing by ID", description = "Get a specific marketplace listing")
    public ResponseEntity<MarketplaceListing> getListingById(@PathVariable UUID id) {
        // This would need to be implemented in MarketplaceListingService
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/listings/{id}")
    @Operation(summary = "Update listing", description = "Update a marketplace listing")
    public ResponseEntity<MarketplaceListing> updateListing(
            @PathVariable UUID id, 
            @RequestBody MarketplaceListing listing) {
        
        // Update the listing (you'll need to implement this service)
        // MarketplaceListing updatedListing = marketplaceListingService.update(id, listing);
        
        return ResponseEntity.ok(listing);
    }

    @DeleteMapping("/listings/{id}")
    @Operation(summary = "Delete listing", description = "Delete a marketplace listing")
    public ResponseEntity<Void> deleteListing(@PathVariable UUID id) {
        // Delete the listing (you'll need to implement this service)
        // marketplaceListingService.delete(id);
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listings/search")
    @Operation(summary = "Search listings", description = "Search marketplace listings")
    public ResponseEntity<List<MarketplaceListing>> searchListings(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) MarketplaceListing.ListingType listingType,
            @RequestParam(required = false) MarketplaceListing.Status status) {
        
        // This would need to be implemented in MarketplaceListingService
        return ResponseEntity.ok(List.of());
    }
} 