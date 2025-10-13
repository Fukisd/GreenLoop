package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Brand;
import org.greenloop.circularfashion.entity.request.BrandCreateRequest;
import org.greenloop.circularfashion.entity.request.BrandUpdateRequest;
import org.greenloop.circularfashion.entity.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BrandService {
    
    // CRUD operations
    BrandResponse createBrand(BrandCreateRequest request);
    BrandResponse getBrandById(UUID id);
    BrandResponse getBrandBySlug(String slug);
    BrandResponse updateBrand(UUID id, BrandUpdateRequest request);
    void deleteBrand(UUID id); // Soft delete (set isActive = false)
    void hardDeleteBrand(UUID id); // Permanent delete
    BrandResponse restoreBrand(UUID id); // Restore soft-deleted brand
    
    // Get operations returning entities (for internal use)
    Brand getEntityById(UUID id);
    Brand getEntityBySlug(String slug);
    
    // Listing and pagination
    Page<BrandResponse> getAllBrands(Pageable pageable);
    List<BrandResponse> getAllActiveBrands();
    List<BrandResponse> getAllInactiveBrands(); // View soft-deleted brands
    Page<BrandResponse> searchBrands(String keyword, Pageable pageable);
    
    // Filtering
    List<BrandResponse> getVerifiedBrands();
    List<BrandResponse> getPartnerBrands();
    List<BrandResponse> getSustainableBrands();
    
    // Statistics
    Long countBrands();
    Long countVerifiedBrands();
    Long countPartnerBrands();
}

