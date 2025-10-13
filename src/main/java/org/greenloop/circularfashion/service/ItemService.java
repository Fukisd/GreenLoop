package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.ItemCreateRequest;
import org.greenloop.circularfashion.entity.request.ItemUpdateRequest;
import org.greenloop.circularfashion.entity.response.ItemResponse;
import org.greenloop.circularfashion.entity.response.ItemSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ItemService {
    
    // CRUD operations with DTOs
    ItemResponse createItem(ItemCreateRequest request, UUID currentUserId);
    ItemResponse getItemById(UUID id);
    ItemResponse updateItem(UUID id, ItemUpdateRequest request);
    void deleteItem(UUID id);
    
    // Get operations returning entities (for internal use)
    Item getEntityById(UUID id);
    Item getEntityByItemCode(String itemCode);
    
    // Pagination and listing
    Page<ItemSummaryResponse> getAllItems(Pageable pageable);
    Page<ItemSummaryResponse> getItemsByOwner(UUID ownerId, Pageable pageable);
    Page<ItemSummaryResponse> getItemsByStatus(Item.ItemStatus status, Pageable pageable);
    Page<ItemSummaryResponse> getItemsByCategory(UUID categoryId, Pageable pageable);
    Page<ItemSummaryResponse> getItemsByBrand(UUID brandId, Pageable pageable);
    
    // Search and filtering
    Page<ItemSummaryResponse> searchItems(String keyword, Pageable pageable);
    Page<ItemSummaryResponse> findItemsWithFilters(
            UUID categoryId,
            List<Item.ItemStatus> statuses,
            BigDecimal minCondition,
            Pageable pageable
    );
    
    // Status management
    ItemResponse updateItemStatus(UUID itemId, Item.ItemStatus newStatus, String reason, UUID userId);
    ItemResponse verifyItem(UUID itemId, UUID verifierId);
    List<Item> getItemsAwaitingVerification();
    List<Item> getMarketplaceReadyItems();
    
    // Ownership management
    ItemResponse transferOwnership(UUID itemId, UUID newOwnerId, UUID currentUserId);
    
    // Condition and valuation
    ItemResponse updateCondition(UUID itemId, BigDecimal conditionScore, String description);
    ItemResponse updateValuation(UUID itemId, BigDecimal estimatedValue);
    
    // Media management
    ItemResponse addImage(UUID itemId, String imageUrl);
    ItemResponse removeImage(UUID itemId, String imageUrl);
    ItemResponse addVideo(UUID itemId, String videoUrl);
    ItemResponse removeVideo(UUID itemId, String videoUrl);
    
    // Tags management
    ItemResponse addTag(UUID itemId, String tag);
    ItemResponse removeTag(UUID itemId, String tag);
    
    // Statistics
    Long countItemsByStatus(Item.ItemStatus status);
    Long countItemsByOwner(UUID ownerId);
    BigDecimal getAverageConditionScore();
    
    // Bulk operations
    List<ItemResponse> createBulkItems(List<ItemCreateRequest> requests, UUID currentUserId);
    void deleteBulkItems(List<UUID> itemIds);
} 