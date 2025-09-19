package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.MarketplaceListing;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.MarketplaceListingCreateRequest;
import org.greenloop.circularfashion.entity.request.MarketplaceListingUpdateRequest;
import org.greenloop.circularfashion.enums.ListingStatus;
import org.greenloop.circularfashion.enums.ListingType;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.MarketplaceListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/listings")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class MarketplaceListingController {

    private final MarketplaceListingService listingService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create listing")
    public ResponseEntity<MarketplaceListing> create(@Valid @RequestBody MarketplaceListingCreateRequest req) {
        User seller = userRepository.findById(req.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        Item item = itemRepository.findById(req.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        MarketplaceListing listing = MarketplaceListing.builder()
                .seller(seller)
                .item(item)
                .listingType(req.getListingType())
                .price(req.getPrice())
                .rentalPricePerDay(req.getRentalPricePerDay())
                .title(req.getTitle())
                .description(req.getDescription())
                .tags(req.getTags())
                .expiresAt(req.getExpiresAt())
                .build();

        MarketplaceListing created = listingService.create(listing);
        return ResponseEntity.created(URI.create("/api/listings/" + created.getListingId())).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get listing by id")
    public ResponseEntity<MarketplaceListing> get(@PathVariable Long id) {
        return listingService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List listings with optional filters")
    public ResponseEntity<List<MarketplaceListing>> list(
            @RequestParam(required = false) Long sellerId,
            @RequestParam(required = false) ListingStatus status,
            @RequestParam(required = false) ListingType type
    ) {
        if (sellerId != null) {
            return ResponseEntity.ok(listingService.getBySeller(sellerId));
        } else if (status != null && type != null) {
            return ResponseEntity.ok(listingService.getByStatusAndType(status, type));
        } else if (status != null) {
            return ResponseEntity.ok(listingService.getByStatus(status));
        } else if (type != null) {
            return ResponseEntity.ok(listingService.getByType(type));
        }
        return ResponseEntity.ok(listingService.getByStatus(ListingStatus.ACTIVE));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update listing")
    public ResponseEntity<MarketplaceListing> update(@PathVariable Long id, @Valid @RequestBody MarketplaceListingUpdateRequest req) {
        MarketplaceListing existing = listingService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (req.getListingType() != null) existing.setListingType(req.getListingType());
        if (req.getPrice() != null) existing.setPrice(req.getPrice());
        if (req.getRentalPricePerDay() != null) existing.setRentalPricePerDay(req.getRentalPricePerDay());
        if (req.getTitle() != null) existing.setTitle(req.getTitle());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getTags() != null) existing.setTags(req.getTags());
        if (req.getStatus() != null) existing.setStatus(req.getStatus());
        if (req.getExpiresAt() != null) existing.setExpiresAt(req.getExpiresAt());

        MarketplaceListing updated = listingService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete listing")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        listingService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 