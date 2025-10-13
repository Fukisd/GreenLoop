package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.request.ItemCreateRequest;
import org.greenloop.circularfashion.entity.request.ItemUpdateRequest;
import org.greenloop.circularfashion.entity.response.ItemResponse;
import org.greenloop.circularfashion.entity.response.ItemStatusUpdateRequest;
import org.greenloop.circularfashion.entity.response.ItemSummaryResponse;
import org.greenloop.circularfashion.service.CloudinaryService;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item Management", description = "Comprehensive APIs for managing circular fashion items")
@SecurityRequirement(name = "bearerAuth")
public class ItemController {

    private final ItemService itemService;
    private final CloudinaryService cloudinaryService;

    // ==================== CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create new item", 
               description = "Create a new item in the system. The authenticated user becomes the owner.")
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody ItemCreateRequest request,
            @RequestParam UUID userId) {
        
        log.info("Creating item: {} for user: {}", request.getName(), userId);
        ItemResponse response = itemService.createItem(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieve detailed information about a specific item")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable UUID id) {
        log.debug("Fetching item: {}", id);
        ItemResponse response = itemService.getItemById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Update an existing item's information")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable UUID id,
            @Valid @RequestBody ItemUpdateRequest request) {
        
        log.info("Updating item: {}", id);
        ItemResponse response = itemService.updateItem(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Delete an item from the system")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        log.info("Deleting item: {}", id);
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Listing & Pagination ====================

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieve all items with pagination and sorting")
    public ResponseEntity<Page<ItemSummaryResponse>> getAllItems(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.debug("Fetching all items");
        Page<ItemSummaryResponse> items = itemService.getAllItems(pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get items by owner", description = "Get all items owned by a specific user")
    public ResponseEntity<Page<ItemSummaryResponse>> getItemsByOwner(
            @PathVariable UUID ownerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        log.debug("Fetching items for owner: {}", ownerId);
        Page<ItemSummaryResponse> items = itemService.getItemsByOwner(ownerId, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get items by status", description = "Get all items with a specific status")
    public ResponseEntity<Page<ItemSummaryResponse>> getItemsByStatus(
            @PathVariable Item.ItemStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Fetching items with status: {}", status);
        Page<ItemSummaryResponse> items = itemService.getItemsByStatus(status, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get items by category", description = "Get all items in a specific category")
    public ResponseEntity<Page<ItemSummaryResponse>> getItemsByCategory(
            @PathVariable UUID categoryId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Fetching items for category: {}", categoryId);
        Page<ItemSummaryResponse> items = itemService.getItemsByCategory(categoryId, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Get items by brand", description = "Get all items from a specific brand")
    public ResponseEntity<Page<ItemSummaryResponse>> getItemsByBrand(
            @PathVariable UUID brandId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Fetching items for brand: {}", brandId);
        Page<ItemSummaryResponse> items = itemService.getItemsByBrand(brandId, pageable);
        return ResponseEntity.ok(items);
    }

    // ==================== Search & Filtering ====================

    @GetMapping("/search")
    @Operation(summary = "Search items", description = "Search items by keyword in name and description")
    public ResponseEntity<Page<ItemSummaryResponse>> searchItems(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Searching items with keyword: {}", keyword);
        Page<ItemSummaryResponse> items = itemService.searchItems(keyword, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter items", description = "Filter items with multiple criteria")
    public ResponseEntity<Page<ItemSummaryResponse>> filterItems(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) List<Item.ItemStatus> statuses,
            @RequestParam(required = false) BigDecimal minCondition,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Filtering items - category: {}, statuses: {}, minCondition: {}", 
                categoryId, statuses, minCondition);
        
        Page<ItemSummaryResponse> items = itemService.findItemsWithFilters(
                categoryId, statuses, minCondition, pageable);
        return ResponseEntity.ok(items);
    }

    // ==================== Status Management ====================

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update item status", description = "Change the status of an item")
    public ResponseEntity<ItemResponse> updateItemStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ItemStatusUpdateRequest request,
            @RequestParam UUID userId) {
        
        log.info("Updating status for item: {} to: {}", id, request.getNewStatus());
        ItemResponse response = itemService.updateItemStatus(
                id, request.getNewStatus(), request.getReason(), userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify item", description = "Mark an item as verified")
    public ResponseEntity<ItemResponse> verifyItem(
            @PathVariable UUID id,
            @RequestParam UUID verifierId) {
        
        log.info("Verifying item: {} by verifier: {}", id, verifierId);
        ItemResponse response = itemService.verifyItem(id, verifierId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/awaiting-verification")
    @Operation(summary = "Get items awaiting verification", 
               description = "Get all items that need to be verified")
    public ResponseEntity<List<Item>> getItemsAwaitingVerification() {
        log.debug("Fetching items awaiting verification");
        List<Item> items = itemService.getItemsAwaitingVerification();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/marketplace-ready")
    @Operation(summary = "Get marketplace ready items", 
               description = "Get all items ready to be listed in marketplace")
    public ResponseEntity<List<Item>> getMarketplaceReadyItems() {
        log.debug("Fetching marketplace ready items");
        List<Item> items = itemService.getMarketplaceReadyItems();
        return ResponseEntity.ok(items);
    }

    // ==================== Ownership Management ====================

    @PostMapping("/{id}/transfer")
    @Operation(summary = "Transfer ownership", description = "Transfer item ownership to another user")
    public ResponseEntity<ItemResponse> transferOwnership(
            @PathVariable UUID id,
            @RequestParam UUID newOwnerId,
            @RequestParam UUID currentUserId) {
        
        log.info("Transferring ownership of item: {} to user: {}", id, newOwnerId);
        ItemResponse response = itemService.transferOwnership(id, newOwnerId, currentUserId);
        return ResponseEntity.ok(response);
    }

    // ==================== Condition & Valuation ====================

    @PatchMapping("/{id}/condition")
    @Operation(summary = "Update condition", description = "Update item condition score and description")
    public ResponseEntity<ItemResponse> updateCondition(
            @PathVariable UUID id,
            @RequestParam BigDecimal conditionScore,
            @RequestParam(required = false) String description) {
        
        log.info("Updating condition for item: {}", id);
        ItemResponse response = itemService.updateCondition(id, conditionScore, description);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/valuation")
    @Operation(summary = "Update valuation", description = "Update item estimated value")
    public ResponseEntity<ItemResponse> updateValuation(
            @PathVariable UUID id,
            @RequestParam BigDecimal estimatedValue) {
        
        log.info("Updating valuation for item: {}", id);
        ItemResponse response = itemService.updateValuation(id, estimatedValue);
        return ResponseEntity.ok(response);
    }

    // ==================== Media Management ====================

    @PostMapping("/{id}/images/upload")
    @Operation(summary = "Upload image", description = "Upload an image file to Cloudinary and add to item")
    public ResponseEntity<ItemResponse> uploadImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        log.info("Uploading image for item: {}", id);
        
        // Upload to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(file, "greenloop/items");
        
        // Add to item
        ItemResponse response = itemService.addImage(id, imageUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/images/upload-multiple")
    @Operation(summary = "Upload multiple images", description = "Upload multiple image files to Cloudinary and add to item")
    public ResponseEntity<ItemResponse> uploadMultipleImages(
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        
        log.info("Uploading {} images for item: {}", files.size(), id);
        
        // Upload all images to Cloudinary
        List<String> imageUrls = cloudinaryService.uploadImages(files, "greenloop/items");
        
        // Add all images to item
        ItemResponse response = itemService.getItemById(id);
        for (String imageUrl : imageUrls) {
            response = itemService.addImage(id, imageUrl);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Add image by URL", description = "Add an image to an item by URL")
    public ResponseEntity<ItemResponse> addImage(
            @PathVariable UUID id,
            @RequestParam String imageUrl) {
        
        log.info("Adding image to item: {}", id);
        ItemResponse response = itemService.addImage(id, imageUrl);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/images")
    @Operation(summary = "Remove image", description = "Remove an image from an item")
    public ResponseEntity<ItemResponse> removeImage(
            @PathVariable UUID id,
            @RequestParam String imageUrl) {
        
        log.info("Removing image from item: {}", id);
        
        // Try to delete from Cloudinary
        try {
            String publicId = cloudinaryService.extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinaryService.deleteImage(publicId);
                log.info("Deleted image from Cloudinary: {}", publicId);
            }
        } catch (Exception e) {
            log.warn("Failed to delete image from Cloudinary: {}", e.getMessage());
            // Continue anyway to remove from item
        }
        
        ItemResponse response = itemService.removeImage(id, imageUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/videos")
    @Operation(summary = "Add video", description = "Add a video to an item")
    public ResponseEntity<ItemResponse> addVideo(
            @PathVariable UUID id,
            @RequestParam String videoUrl) {
        
        log.info("Adding video to item: {}", id);
        ItemResponse response = itemService.addVideo(id, videoUrl);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/videos")
    @Operation(summary = "Remove video", description = "Remove a video from an item")
    public ResponseEntity<ItemResponse> removeVideo(
            @PathVariable UUID id,
            @RequestParam String videoUrl) {
        
        log.info("Removing video from item: {}", id);
        ItemResponse response = itemService.removeVideo(id, videoUrl);
        return ResponseEntity.ok(response);
    }

    // ==================== Tags Management ====================

    @PostMapping("/{id}/tags")
    @Operation(summary = "Add tag", description = "Add a tag to an item")
    public ResponseEntity<ItemResponse> addTag(
            @PathVariable UUID id,
            @RequestParam String tag) {
        
        log.info("Adding tag '{}' to item: {}", tag, id);
        ItemResponse response = itemService.addTag(id, tag);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/tags")
    @Operation(summary = "Remove tag", description = "Remove a tag from an item")
    public ResponseEntity<ItemResponse> removeTag(
            @PathVariable UUID id,
            @RequestParam String tag) {
        
        log.info("Removing tag '{}' from item: {}", tag, id);
        ItemResponse response = itemService.removeTag(id, tag);
        return ResponseEntity.ok(response);
    }

    // ==================== Statistics ====================

    @GetMapping("/statistics")
    @Operation(summary = "Get statistics", description = "Get various statistics about items")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.debug("Fetching item statistics");
        
        BigDecimal avgCondition = itemService.getAverageConditionScore();
        
        return ResponseEntity.ok(Map.of(
                "averageConditionScore", avgCondition
        ));
    }

    @GetMapping("/statistics/owner/{ownerId}/count")
    @Operation(summary = "Count items by owner", description = "Get count of items owned by a user")
    public ResponseEntity<Long> countItemsByOwner(@PathVariable UUID ownerId) {
        log.debug("Counting items for owner: {}", ownerId);
        Long count = itemService.countItemsByOwner(ownerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/status/{status}/count")
    @Operation(summary = "Count items by status", description = "Get count of items with specific status")
    public ResponseEntity<Long> countItemsByStatus(@PathVariable Item.ItemStatus status) {
        log.debug("Counting items with status: {}", status);
        Long count = itemService.countItemsByStatus(status);
        return ResponseEntity.ok(count);
    }

    // ==================== Bulk Operations ====================

    @PostMapping("/bulk")
    @Operation(summary = "Create items in bulk", description = "Create multiple items at once")
    public ResponseEntity<List<ItemResponse>> createBulkItems(
            @Valid @RequestBody List<ItemCreateRequest> requests,
            @RequestParam UUID userId) {
        
        log.info("Creating {} items in bulk", requests.size());
        List<ItemResponse> responses = itemService.createBulkItems(requests, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @DeleteMapping("/bulk")
    @Operation(summary = "Delete items in bulk", description = "Delete multiple items at once")
    public ResponseEntity<Void> deleteBulkItems(@RequestBody List<UUID> itemIds) {
        log.info("Deleting {} items in bulk", itemIds.size());
        itemService.deleteBulkItems(itemIds);
        return ResponseEntity.noContent().build();
    }
} 