package org.greenloop.circularfashion.mapper;

import org.greenloop.circularfashion.entity.Brand;
import org.greenloop.circularfashion.entity.request.BrandCreateRequest;
import org.greenloop.circularfashion.entity.request.BrandUpdateRequest;
import org.greenloop.circularfashion.entity.response.BrandResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandMapper {
    
    /**
     * Convert Brand entity to BrandResponse DTO
     */
    public BrandResponse toResponse(Brand brand) {
        return toResponse(brand, null);
    }
    
    /**
     * Convert Brand entity to BrandResponse DTO with item count
     */
    public BrandResponse toResponse(Brand brand, Long itemCount) {
        if (brand == null) {
            return null;
        }
        
        return BrandResponse.builder()
                .brandId(brand.getBrandId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .description(brand.getDescription())
                .logoUrl(brand.getLogoUrl())
                .website(brand.getWebsite())
                .sustainabilityRating(brand.getSustainabilityRating())
                .ecoCertification(brand.getEcoCertification())
                .isVerified(brand.getIsVerified())
                .isPartner(brand.getIsPartner())
                .isActive(brand.getIsActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .isSustainable(brand.isSustainable())
                .hasEcoCertification(brand.hasEcoCertification())
                .totalItems(itemCount)
                .build();
    }
    
    /**
     * Convert BrandCreateRequest to Brand entity
     */
    public Brand toEntity(BrandCreateRequest request) {
        if (request == null) {
            return null;
        }
        
        return Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .website(request.getWebsite())
                .sustainabilityRating(request.getSustainabilityRating() != null 
                        ? request.getSustainabilityRating() 
                        : BigDecimal.ZERO)
                .ecoCertification(request.getEcoCertification())
                .isVerified(request.getIsVerified() != null ? request.getIsVerified() : false)
                .isPartner(request.getIsPartner() != null ? request.getIsPartner() : false)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }
    
    /**
     * Update existing Brand entity with data from BrandUpdateRequest
     */
    public void updateEntity(Brand brand, BrandUpdateRequest request) {
        if (brand == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            brand.setName(request.getName());
        }
        if (request.getDescription() != null) {
            brand.setDescription(request.getDescription());
        }
        if (request.getLogoUrl() != null) {
            brand.setLogoUrl(request.getLogoUrl());
        }
        if (request.getWebsite() != null) {
            brand.setWebsite(request.getWebsite());
        }
        if (request.getSustainabilityRating() != null) {
            brand.setSustainabilityRating(request.getSustainabilityRating());
        }
        if (request.getEcoCertification() != null) {
            brand.setEcoCertification(request.getEcoCertification());
        }
        if (request.getIsVerified() != null) {
            brand.setIsVerified(request.getIsVerified());
        }
        if (request.getIsPartner() != null) {
            brand.setIsPartner(request.getIsPartner());
        }
        if (request.getIsActive() != null) {
            brand.setIsActive(request.getIsActive());
        }
    }
    
    /**
     * Convert list of Brands to list of BrandResponse
     */
    public List<BrandResponse> toResponseList(List<Brand> brands) {
        if (brands == null) {
            return List.of();
        }
        return brands.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}









