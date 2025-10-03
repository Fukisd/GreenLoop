package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Item Management", description = "APIs for managing items")
public class ItemController {

    private final ItemService itemService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieve all items with pagination")
    public ResponseEntity<Page<Item>> getAllItems(Pageable pageable) {
        // This would need to be implemented in ItemService
        return ResponseEntity.ok(Page.empty());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieve a specific item by its ID")
    public ResponseEntity<Item> getItemById(@PathVariable UUID id) {
        Optional<Item> item = itemService.getById(id);
        return item.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new item", description = "Create a new item")
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        Item createdItem = itemService.create(item);
        return ResponseEntity.ok(createdItem);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update item", description = "Update an existing item")
    public ResponseEntity<Item> updateItem(@PathVariable UUID id, @RequestBody Item item) {
        Item updatedItem = itemService.update(id, item);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete item", description = "Delete an item by ID")
    public ResponseEntity<Void> deleteItem(@PathVariable UUID id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "Get items by owner", description = "Get all items owned by a specific user")
    public ResponseEntity<List<Item>> getItemsByOwner(@PathVariable UUID ownerId) {
        List<Item> items = itemService.getByOwner(ownerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get items by status", description = "Get all items with a specific status")
    public ResponseEntity<List<Item>> getItemsByStatus(@PathVariable Item.ItemStatus status) {
        List<Item> items = itemService.getByStatus(status);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/owner/{ownerId}/status/{status}")
    @Operation(summary = "Get items by owner and status", description = "Get items owned by a user with specific status")
    public ResponseEntity<List<Item>> getItemsByOwnerAndStatus(
            @PathVariable UUID ownerId, 
            @PathVariable Item.ItemStatus status) {
        List<Item> items = itemService.getByOwnerAndStatus(ownerId, status);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get items by category", description = "Get all items in a specific category")
    public ResponseEntity<List<Item>> getItemsByCategory(@PathVariable UUID categoryId) {
        List<Item> items = itemService.getByCategory(categoryId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/brand/{brandId}")
    @Operation(summary = "Get items by brand", description = "Get all items from a specific brand")
    public ResponseEntity<List<Item>> getItemsByBrand(@PathVariable UUID brandId) {
        List<Item> items = itemService.getByBrand(brandId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    @Operation(summary = "Search items", description = "Search items by various criteria")
    public ResponseEntity<List<Item>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Item.ItemStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId) {
        
        // This would need to be implemented in ItemService
        return ResponseEntity.ok(List.of());
    }
} 