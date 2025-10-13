package org.greenloop.circularfashion.mapper;

import org.greenloop.circularfashion.entity.*;
import org.greenloop.circularfashion.entity.request.ItemCreateRequest;
import org.greenloop.circularfashion.entity.request.ItemUpdateRequest;
import org.greenloop.circularfashion.entity.response.ItemResponse;
import org.greenloop.circularfashion.entity.response.ItemSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {
    
    /**
     * Convert Item entity to ItemResponse DTO
     */
    public ItemResponse toResponse(Item item) {
        if (item == null) {
            return null;
        }
        
        return ItemResponse.builder()
                .itemId(item.getItemId())
                .itemCode(item.getItemCode())
                .name(item.getName())
                .description(item.getDescription())
                .displayName(item.getDisplayName())
                // Category
                .categoryId(item.getCategory() != null ? item.getCategory().getCategoryId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .categorySlug(item.getCategory() != null ? item.getCategory().getSlug() : null)
                // Brand
                .brandId(item.getBrand() != null ? item.getBrand().getBrandId() : null)
                .brandName(item.getBrand() != null ? item.getBrand().getName() : null)
                .brandLogoUrl(item.getBrand() != null ? item.getBrand().getLogoUrl() : null)
                // Physical properties
                .size(item.getSize())
                .color(item.getColor())
                .materialComposition(item.getMaterialComposition())
                .weightGrams(item.getWeightGrams())
                .dimensions(item.getDimensions())
                // Condition and valuation
                .conditionScore(item.getConditionScore())
                .conditionText(item.getConditionText())
                .conditionDescription(item.getConditionDescription())
                .originalPrice(item.getOriginalPrice())
                .currentEstimatedValue(item.getCurrentEstimatedValue())
                // Ownership
                .originalOwnerId(item.getOriginalOwner() != null ? item.getOriginalOwner().getUserId() : null)
                .originalOwnerName(item.getOriginalOwner() != null ? item.getOriginalOwner().getUsername() : null)
                .currentOwnerId(item.getCurrentOwner() != null ? item.getCurrentOwner().getUserId() : null)
                .currentOwnerName(item.getCurrentOwner() != null ? item.getCurrentOwner().getUsername() : null)
                .acquisitionMethod(item.getAcquisitionMethod())
                // Status
                .itemStatus(item.getItemStatus())
                .isVerified(item.getIsVerified())
                .verificationDate(item.getVerificationDate())
                .verifiedById(item.getVerifiedBy() != null ? item.getVerifiedBy().getUserId() : null)
                .verifiedByName(item.getVerifiedBy() != null ? item.getVerifiedBy().getUsername() : null)
                // Sustainability
                .carbonFootprintKg(item.getCarbonFootprintKg())
                .waterSavedLiters(item.getWaterSavedLiters())
                .energySavedKwh(item.getEnergySavedKwh())
                .isSustainable(item.isSustainable())
                // Media
                .images(item.getImages())
                .videos(item.getVideos())
                .primaryImageUrl(item.getPrimaryImageUrl())
                // Metadata
                .tags(item.getTags())
                .metadata(item.getMetadata())
                // Timestamps
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                // Computed flags
                .availableForListing(item.isAvailableForListing())
                .inMarketplace(item.isInMarketplace())
                .hasImages(item.hasImages())
                .hasVideos(item.hasVideos())
                .build();
    }
    
    /**
     * Convert Item entity to ItemSummaryResponse DTO (lightweight)
     */
    public ItemSummaryResponse toSummaryResponse(Item item) {
        if (item == null) {
            return null;
        }
        
        return ItemSummaryResponse.builder()
                .itemId(item.getItemId())
                .itemCode(item.getItemCode())
                .name(item.getName())
                .displayName(item.getDisplayName())
                .categoryId(item.getCategory() != null ? item.getCategory().getCategoryId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .brandId(item.getBrand() != null ? item.getBrand().getBrandId() : null)
                .brandName(item.getBrand() != null ? item.getBrand().getName() : null)
                .size(item.getSize())
                .color(item.getColor())
                .conditionScore(item.getConditionScore())
                .conditionText(item.getConditionText())
                .currentEstimatedValue(item.getCurrentEstimatedValue())
                .itemStatus(item.getItemStatus())
                .isVerified(item.getIsVerified())
                .primaryImageUrl(item.getPrimaryImageUrl())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
    
    /**
     * Convert ItemCreateRequest to Item entity
     */
    public Item toEntity(ItemCreateRequest request, Category category, Brand brand, User currentOwner) {
        if (request == null) {
            return null;
        }
        
        Item.ItemBuilder builder = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .brand(brand)
                .size(request.getSize())
                .color(request.getColor())
                .materialComposition(request.getMaterialComposition())
                .weightGrams(request.getWeightGrams())
                .dimensions(request.getDimensions())
                .conditionScore(request.getConditionScore())
                .conditionDescription(request.getConditionDescription())
                .originalPrice(request.getOriginalPrice())
                .currentEstimatedValue(request.getCurrentEstimatedValue())
                .acquisitionMethod(request.getAcquisitionMethod() != null 
                    ? Item.AcquisitionMethod.valueOf(request.getAcquisitionMethod().toUpperCase()) 
                    : null)
                .currentOwner(currentOwner)
                .originalOwner(currentOwner) // Initially, current owner is original owner
                .images(request.getImages() != null ? new ArrayList<>(request.getImages()) : new ArrayList<>())
                .videos(request.getVideos() != null ? new ArrayList<>(request.getVideos()) : new ArrayList<>())
                .tags(request.getTags() != null ? new ArrayList<>(request.getTags()) : new ArrayList<>())
                .metadata(request.getMetadata())
                .carbonFootprintKg(request.getCarbonFootprintKg())
                .waterSavedLiters(request.getWaterSavedLiters())
                .energySavedKwh(request.getEnergySavedKwh())
                .itemStatus(Item.ItemStatus.SUBMITTED)
                .isVerified(false);
        
        return builder.build();
    }
    
    /**
     * Update existing Item entity with data from ItemUpdateRequest
     */
    public void updateEntity(Item item, ItemUpdateRequest request, Category category, Brand brand) {
        if (item == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (category != null) {
            item.setCategory(category);
        }
        if (brand != null) {
            item.setBrand(brand);
        }
        if (request.getSize() != null) {
            item.setSize(request.getSize());
        }
        if (request.getColor() != null) {
            item.setColor(request.getColor());
        }
        if (request.getMaterialComposition() != null) {
            item.setMaterialComposition(request.getMaterialComposition());
        }
        if (request.getWeightGrams() != null) {
            item.setWeightGrams(request.getWeightGrams());
        }
        if (request.getDimensions() != null) {
            item.setDimensions(request.getDimensions());
        }
        if (request.getConditionScore() != null) {
            item.setConditionScore(request.getConditionScore());
        }
        if (request.getConditionDescription() != null) {
            item.setConditionDescription(request.getConditionDescription());
        }
        if (request.getOriginalPrice() != null) {
            item.setOriginalPrice(request.getOriginalPrice());
        }
        if (request.getCurrentEstimatedValue() != null) {
            item.setCurrentEstimatedValue(request.getCurrentEstimatedValue());
        }
        if (request.getAcquisitionMethod() != null) {
            item.setAcquisitionMethod(Item.AcquisitionMethod.valueOf(request.getAcquisitionMethod().toUpperCase()));
        }
        if (request.getImages() != null) {
            item.setImages(new ArrayList<>(request.getImages()));
        }
        if (request.getVideos() != null) {
            item.setVideos(new ArrayList<>(request.getVideos()));
        }
        if (request.getTags() != null) {
            item.setTags(new ArrayList<>(request.getTags()));
        }
        if (request.getMetadata() != null) {
            item.setMetadata(request.getMetadata());
        }
        if (request.getCarbonFootprintKg() != null) {
            item.setCarbonFootprintKg(request.getCarbonFootprintKg());
        }
        if (request.getWaterSavedLiters() != null) {
            item.setWaterSavedLiters(request.getWaterSavedLiters());
        }
        if (request.getEnergySavedKwh() != null) {
            item.setEnergySavedKwh(request.getEnergySavedKwh());
        }
    }
    
    /**
     * Convert list of Items to list of ItemResponse
     */
    public List<ItemResponse> toResponseList(List<Item> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert list of Items to list of ItemSummaryResponse
     */
    public List<ItemSummaryResponse> toSummaryResponseList(List<Item> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(this::toSummaryResponse)
                .collect(Collectors.toList());
    }
}





