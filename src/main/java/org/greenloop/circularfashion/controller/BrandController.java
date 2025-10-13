package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.request.BrandCreateRequest;
import org.greenloop.circularfashion.entity.request.BrandUpdateRequest;
import org.greenloop.circularfashion.entity.response.BrandResponse;
import org.greenloop.circularfashion.service.BrandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Brand Management", description = "APIs for managing brands")
@SecurityRequirement(name = "bearerAuth")
public class BrandController {

    private final BrandService brandService;

    // ==================== CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create new brand", description = "Create a new brand in the system")
    public ResponseEntity<BrandResponse> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        log.info("Creating brand: {}", request.getName());
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand by ID", description = "Retrieve detailed information about a specific brand")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable UUID id) {
        log.debug("Fetching brand: {}", id);
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get brand by slug", description = "Retrieve brand information by slug")
    public ResponseEntity<BrandResponse> getBrandBySlug(@PathVariable String slug) {
        log.debug("Fetching brand by slug: {}", slug);
        BrandResponse response = brandService.getBrandBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update brand", description = "Update an existing brand's information")
    public ResponseEntity<BrandResponse> updateBrand(
            @PathVariable UUID id,
            @Valid @RequestBody BrandUpdateRequest request) {
        
        log.info("Updating brand: {}", id);
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete brand (soft)", description = "Soft delete a brand by setting isActive=false")
    public ResponseEntity<Void> deleteBrand(@PathVariable UUID id) {
        log.info("Soft deleting brand: {}", id);
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Delete brand permanently", 
               description = "Permanently delete a brand from the database (admin only)")
    public ResponseEntity<Void> hardDeleteBrand(@PathVariable UUID id) {
        log.warn("Hard deleting brand: {}", id);
        brandService.hardDeleteBrand(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore brand", description = "Restore a soft-deleted brand by setting isActive=true")
    public ResponseEntity<BrandResponse> restoreBrand(@PathVariable UUID id) {
        log.info("Restoring brand: {}", id);
        BrandResponse response = brandService.restoreBrand(id);
        return ResponseEntity.ok(response);
    }

    // ==================== Listing & Pagination ====================

    @GetMapping
    @Operation(summary = "Get all brands", description = "Retrieve all brands with pagination")
    public ResponseEntity<Page<BrandResponse>> getAllBrands(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching all brands");
        Page<BrandResponse> brands = brandService.getAllBrands(pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active brands", description = "Retrieve all active brands")
    public ResponseEntity<List<BrandResponse>> getAllActiveBrands() {
        log.debug("Fetching active brands");
        List<BrandResponse> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive brands", description = "Retrieve all soft-deleted brands (admin only)")
    public ResponseEntity<List<BrandResponse>> getAllInactiveBrands() {
        log.debug("Fetching inactive brands");
        List<BrandResponse> brands = brandService.getAllInactiveBrands();
        return ResponseEntity.ok(brands);
    }

    // ==================== Search & Filtering ====================

    @GetMapping("/search")
    @Operation(summary = "Search brands", description = "Search brands by keyword in name and description")
    public ResponseEntity<Page<BrandResponse>> searchBrands(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Searching brands with keyword: {}", keyword);
        Page<BrandResponse> brands = brandService.searchBrands(keyword, pageable);
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/verified")
    @Operation(summary = "Get verified brands", description = "Get all verified brands")
    public ResponseEntity<List<BrandResponse>> getVerifiedBrands() {
        log.debug("Fetching verified brands");
        List<BrandResponse> brands = brandService.getVerifiedBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/partners")
    @Operation(summary = "Get partner brands", description = "Get all partner brands")
    public ResponseEntity<List<BrandResponse>> getPartnerBrands() {
        log.debug("Fetching partner brands");
        List<BrandResponse> brands = brandService.getPartnerBrands();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/sustainable")
    @Operation(summary = "Get sustainable brands", description = "Get all sustainable brands")
    public ResponseEntity<List<BrandResponse>> getSustainableBrands() {
        log.debug("Fetching sustainable brands");
        List<BrandResponse> brands = brandService.getSustainableBrands();
        return ResponseEntity.ok(brands);
    }

    // ==================== Statistics ====================

    @GetMapping("/statistics")
    @Operation(summary = "Get brand statistics", description = "Get various statistics about brands")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.debug("Fetching brand statistics");
        
        Long totalBrands = brandService.countBrands();
        Long verifiedBrands = brandService.countVerifiedBrands();
        Long partnerBrands = brandService.countPartnerBrands();
        
        return ResponseEntity.ok(Map.of(
                "totalBrands", totalBrands,
                "verifiedBrands", verifiedBrands,
                "partnerBrands", partnerBrands
        ));
    }
}

