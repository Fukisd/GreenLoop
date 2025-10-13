package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.*;
import org.greenloop.circularfashion.entity.request.ItemCreateRequest;
import org.greenloop.circularfashion.entity.request.ItemUpdateRequest;
import org.greenloop.circularfashion.entity.response.ItemResponse;
import org.greenloop.circularfashion.entity.response.ItemSummaryResponse;
import org.greenloop.circularfashion.exception.InvalidStatusTransitionException;
import org.greenloop.circularfashion.exception.ItemNotFoundException;
import org.greenloop.circularfashion.exception.ResourceNotFoundException;
import org.greenloop.circularfashion.exception.UnauthorizedItemAccessException;
import org.greenloop.circularfashion.mapper.ItemMapper;
import org.greenloop.circularfashion.repository.BrandRepository;
import org.greenloop.circularfashion.repository.CategoryRepository;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemResponse createItem(ItemCreateRequest request, UUID currentUserId) {
        log.info("Creating item: {} for user: {}", request.getName(), currentUserId);
        
        // Fetch required entities
        User currentOwner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
        
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        
        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
        }
        
        // Convert request to entity
        Item item = itemMapper.toEntity(request, category, brand, currentOwner);
        
        // Save and return response
        Item savedItem = itemRepository.save(item);
        log.info("Item created successfully with id: {}", savedItem.getItemId());
        
        return itemMapper.toResponse(savedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponse getItemById(UUID id) {
        log.debug("Fetching item by id: {}", id);
        Item item = getEntityById(id);
        return itemMapper.toResponse(item);
    }

    @Override
    public ItemResponse updateItem(UUID id, ItemUpdateRequest request) {
        log.info("Updating item: {}", id);
        
        Item item = getEntityById(id);
        
        // Fetch optional entities if provided
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }
        
        Brand brand = null;
        if (request.getBrandId() != null) {
            brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand", "id", request.getBrandId()));
        }
        
        // Update entity
        itemMapper.updateEntity(item, request, category, brand);
        
        // Handle status update if provided
        if (request.getItemStatus() != null) {
            Item.ItemStatus newStatus = Item.ItemStatus.valueOf(request.getItemStatus().toUpperCase());
            if (newStatus != item.getItemStatus()) {
                item.setItemStatus(newStatus);
            }
        }
        
        Item updatedItem = itemRepository.save(item);
        log.info("Item updated successfully: {}", id);
        
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public void deleteItem(UUID id) {
        log.info("Deleting item: {}", id);
        
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        
        itemRepository.deleteById(id);
        log.info("Item deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getEntityById(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Item getEntityByItemCode(String itemCode) {
        return itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new ItemNotFoundException(itemCode));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getAllItems(Pageable pageable) {
        log.debug("Fetching all items with pagination");
        Page<Item> items = itemRepository.findAll(pageable);
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItemsByOwner(UUID ownerId, Pageable pageable) {
        log.debug("Fetching items for owner: {}", ownerId);
        
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        
        Page<Item> items = itemRepository.findByCurrentOwnerOrderByCreatedAtDesc(owner, pageable);
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItemsByStatus(Item.ItemStatus status, Pageable pageable) {
        log.debug("Fetching items with status: {}", status);
        Page<Item> items = itemRepository.findByStatusAndVerified(status, pageable);
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItemsByCategory(UUID categoryId, Pageable pageable) {
        log.debug("Fetching items for category: {}", categoryId);
        
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        
        Page<Item> items = itemRepository.findByCategoryAndStatus(
                categoryId, 
                Item.ItemStatus.LISTED, 
                pageable
        );
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> getItemsByBrand(UUID brandId, Pageable pageable) {
        log.debug("Fetching items for brand: {}", brandId);
        
        if (!brandRepository.existsById(brandId)) {
            throw new ResourceNotFoundException("Brand", "id", brandId);
        }
        
        List<Item> items = itemRepository.findByBrandId(brandId);
        
        // Manual pagination (could be optimized with custom query)
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), items.size());
        List<ItemSummaryResponse> pageContent = items.subList(start, end).stream()
                .map(itemMapper::toSummaryResponse)
                .collect(Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, items.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> searchItems(String keyword, Pageable pageable) {
        log.debug("Searching items with keyword: {}", keyword);
        Page<Item> items = itemRepository.searchItems(keyword, pageable);
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemSummaryResponse> findItemsWithFilters(
            UUID categoryId, 
            List<Item.ItemStatus> statuses, 
            BigDecimal minCondition, 
            Pageable pageable) {
        
        log.debug("Finding items with filters - category: {}, statuses: {}, minCondition: {}", 
                categoryId, statuses, minCondition);
        
        Page<Item> items = itemRepository.findWithFilters(categoryId, statuses, minCondition, pageable);
        return items.map(itemMapper::toSummaryResponse);
    }

    @Override
    public ItemResponse updateItemStatus(UUID itemId, Item.ItemStatus newStatus, String reason, UUID userId) {
        log.info("Updating item {} status to: {} by user: {}", itemId, newStatus, userId);
        
        Item item = getEntityById(itemId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Validate status transition
        validateStatusTransition(item.getItemStatus(), newStatus);
        
        // Update status with lifecycle tracking
        item.updateStatus(newStatus, user, reason);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Item status updated successfully");
        
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse verifyItem(UUID itemId, UUID verifierId) {
        log.info("Verifying item: {} by verifier: {}", itemId, verifierId);
        
        Item item = getEntityById(itemId);
        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", verifierId));
        
        item.setIsVerified(true);
        item.setVerificationDate(LocalDateTime.now());
        item.setVerifiedBy(verifier);
        
        // Auto-update status if appropriate
        if (item.getItemStatus() == Item.ItemStatus.COLLECTED) {
            item.setItemStatus(Item.ItemStatus.VALUED);
        }
        
        Item verifiedItem = itemRepository.save(item);
        log.info("Item verified successfully");
        
        return itemMapper.toResponse(verifiedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getItemsAwaitingVerification() {
        return itemRepository.findUnverifiedCollectedItems();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getMarketplaceReadyItems() {
        return itemRepository.findMarketplaceReadyItems(Pageable.unpaged()).getContent();
    }

    @Override
    public ItemResponse transferOwnership(UUID itemId, UUID newOwnerId, UUID currentUserId) {
        log.info("Transferring ownership of item: {} from user: {} to user: {}", 
                itemId, currentUserId, newOwnerId);
        
        Item item = getEntityById(itemId);
        
        // Verify current user is the owner
        if (!item.getCurrentOwner().getUserId().equals(currentUserId)) {
            throw new UnauthorizedItemAccessException("Only current owner can transfer ownership");
        }
        
        User newOwner = userRepository.findById(newOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", newOwnerId));
        
        item.setCurrentOwner(newOwner);
        
        Item updatedItem = itemRepository.save(item);
        log.info("Ownership transferred successfully");
        
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse updateCondition(UUID itemId, BigDecimal conditionScore, String description) {
        log.info("Updating condition for item: {}", itemId);
        
        Item item = getEntityById(itemId);
        item.setConditionScore(conditionScore);
        item.setConditionDescription(description);
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse updateValuation(UUID itemId, BigDecimal estimatedValue) {
        log.info("Updating valuation for item: {}", itemId);
        
        Item item = getEntityById(itemId);
        item.setCurrentEstimatedValue(estimatedValue);
        
        // Auto-update status if needed
        if (item.getItemStatus() == Item.ItemStatus.VALUING) {
            item.setItemStatus(Item.ItemStatus.VALUED);
        }
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse addImage(UUID itemId, String imageUrl) {
        log.info("Adding image to item: {}", itemId);
        
        Item item = getEntityById(itemId);
        
        if (item.getImages() == null) {
            item.setImages(new ArrayList<>());
        }
        
        if (!item.getImages().contains(imageUrl)) {
            item.getImages().add(imageUrl);
        }
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse removeImage(UUID itemId, String imageUrl) {
        log.info("Removing image from item: {}", itemId);
        
        Item item = getEntityById(itemId);
        
        if (item.getImages() != null) {
            item.getImages().remove(imageUrl);
        }
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse addVideo(UUID itemId, String videoUrl) {
        log.info("Adding video to item: {}", itemId);
        
        Item item = getEntityById(itemId);
        
        if (item.getVideos() == null) {
            item.setVideos(new ArrayList<>());
        }
        
        if (!item.getVideos().contains(videoUrl)) {
            item.getVideos().add(videoUrl);
        }
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse removeVideo(UUID itemId, String videoUrl) {
        log.info("Removing video from item: {}", itemId);
        
        Item item = getEntityById(itemId);
        
        if (item.getVideos() != null) {
            item.getVideos().remove(videoUrl);
        }
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse addTag(UUID itemId, String tag) {
        log.info("Adding tag '{}' to item: {}", tag, itemId);
        
        Item item = getEntityById(itemId);
        item.addTag(tag);
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    public ItemResponse removeTag(UUID itemId, String tag) {
        log.info("Removing tag '{}' from item: {}", tag, itemId);
        
        Item item = getEntityById(itemId);
        item.removeTag(tag);
        
        Item updatedItem = itemRepository.save(item);
        return itemMapper.toResponse(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countItemsByStatus(Item.ItemStatus status) {
        return (long) itemRepository.findByItemStatus(status).size();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countItemsByOwner(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", ownerId));
        return (long) itemRepository.findByCurrentOwner(owner).size();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageConditionScore() {
        BigDecimal avg = itemRepository.getAverageConditionScore();
        return avg != null ? avg : BigDecimal.ZERO;
    }

    @Override
    public List<ItemResponse> createBulkItems(List<ItemCreateRequest> requests, UUID currentUserId) {
        log.info("Creating {} items in bulk for user: {}", requests.size(), currentUserId);
        
        List<ItemResponse> responses = new ArrayList<>();
        
        for (ItemCreateRequest request : requests) {
            try {
                ItemResponse response = createItem(request, currentUserId);
                responses.add(response);
            } catch (Exception e) {
                log.error("Error creating item '{}': {}", request.getName(), e.getMessage());
                // Continue with next item
            }
        }
        
        log.info("Successfully created {} out of {} items", responses.size(), requests.size());
        return responses;
    }

    @Override
    public void deleteBulkItems(List<UUID> itemIds) {
        log.info("Deleting {} items in bulk", itemIds.size());
        
        for (UUID itemId : itemIds) {
            try {
                deleteItem(itemId);
            } catch (Exception e) {
                log.error("Error deleting item {}: {}", itemId, e.getMessage());
                // Continue with next item
            }
        }
        
        log.info("Bulk delete completed");
    }

    /**
     * Validate status transition rules
     */
    private void validateStatusTransition(Item.ItemStatus currentStatus, Item.ItemStatus newStatus) {
        // Define valid transitions
        // This is a simplified version - you can make it more sophisticated
        
        if (currentStatus == newStatus) {
            return; // Same status, no validation needed
        }
        
        // Example validations
        if (currentStatus == Item.ItemStatus.SOLD && newStatus != Item.ItemStatus.SOLD) {
            throw new InvalidStatusTransitionException("Cannot change status of a sold item");
        }
        
        if (currentStatus == Item.ItemStatus.RECYCLED && newStatus != Item.ItemStatus.RECYCLED) {
            throw new InvalidStatusTransitionException("Cannot change status of a recycled item");
        }
        
        if (currentStatus == Item.ItemStatus.REJECTED) {
            throw new InvalidStatusTransitionException("Cannot change status of a rejected item");
        }
        
        // Add more validation rules as needed
    }
} 