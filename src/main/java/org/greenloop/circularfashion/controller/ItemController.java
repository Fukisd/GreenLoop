package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.greenloop.circularfashion.entity.Brand;
import org.greenloop.circularfashion.entity.Category;
import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.User;
import org.greenloop.circularfashion.entity.request.ItemCreateRequest;
import org.greenloop.circularfashion.entity.request.ItemUpdateRequest;
import org.greenloop.circularfashion.enums.ItemStatus;
import org.greenloop.circularfashion.repository.BrandRepository;
import org.greenloop.circularfashion.repository.CategoryRepository;
import org.greenloop.circularfashion.repository.UserRepository;
import org.greenloop.circularfashion.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/items")
@CrossOrigin("*")
@SecurityRequirement(name = "api")
public class ItemController {

    private final ItemService itemService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create item")
    public ResponseEntity<Item> create(@Valid @RequestBody ItemCreateRequest req) {
        User owner = userRepository.findById(req.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
        Category category = req.getCategoryId() == null ? null : categoryRepository.findById(req.getCategoryId())
                .orElse(null);
        Brand brand = req.getBrandId() == null ? null : brandRepository.findById(req.getBrandId())
                .orElse(null);

        Item item = Item.builder()
                .owner(owner)
                .currentHolder(owner)
                .category(category)
                .brand(brand)
                .name(req.getName())
                .description(req.getDescription())
                .size(req.getSize())
                .color(req.getColor())
                .conditionRating(req.getConditionRating())
                .conditionDescription(req.getConditionDescription())
                .purchaseDate(req.getPurchaseDate())
                .originalPrice(req.getOriginalPrice())
                .currentStatus(ItemStatus.OWNED)
                .location(req.getLocation())
                .images(req.getImages())
                .materialComposition(req.getMaterialComposition())
                .careInstructions(req.getCareInstructions())
                .sustainabilityMetrics(req.getSustainabilityMetrics())
                .rfidTag(req.getRfidTag())
                .qrCode(req.getQrCode())
                .sku(req.getSku())
                .build();

        Item created = itemService.create(item);
        return ResponseEntity.created(URI.create("/api/items/" + created.getItemId())).body(created);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get item by id")
    public ResponseEntity<Item> get(@PathVariable Long id) {
        return itemService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List items with optional filters")
    public ResponseEntity<List<Item>> list(
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId
    ) {
        if (ownerId != null && status != null) {
            return ResponseEntity.ok(itemService.getByOwnerAndStatus(ownerId, status));
        } else if (ownerId != null) {
            return ResponseEntity.ok(itemService.getByOwner(ownerId));
        } else if (status != null) {
            return ResponseEntity.ok(itemService.getByStatus(status));
        } else if (categoryId != null) {
            return ResponseEntity.ok(itemService.getByCategory(categoryId));
        } else if (brandId != null) {
            return ResponseEntity.ok(itemService.getByBrand(brandId));
        }
        return ResponseEntity.ok(itemService.getByStatus(ItemStatus.OWNED));
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update item")
    public ResponseEntity<Item> update(@PathVariable Long id, @Valid @RequestBody ItemUpdateRequest req) {
        Item existing = itemService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        if (req.getCategoryId() != null) {
            categoryRepository.findById(req.getCategoryId()).ifPresent(existing::setCategory);
        }
        if (req.getBrandId() != null) {
            brandRepository.findById(req.getBrandId()).ifPresent(existing::setBrand);
        }
        if (req.getName() != null) existing.setName(req.getName());
        if (req.getDescription() != null) existing.setDescription(req.getDescription());
        if (req.getSize() != null) existing.setSize(req.getSize());
        if (req.getColor() != null) existing.setColor(req.getColor());
        if (req.getConditionRating() != null) existing.setConditionRating(req.getConditionRating());
        if (req.getConditionDescription() != null) existing.setConditionDescription(req.getConditionDescription());
        if (req.getPurchaseDate() != null) existing.setPurchaseDate(req.getPurchaseDate());
        if (req.getOriginalPrice() != null) existing.setOriginalPrice(req.getOriginalPrice());
        if (req.getLocation() != null) existing.setLocation(req.getLocation());
        if (req.getImages() != null) existing.setImages(req.getImages());
        if (req.getMaterialComposition() != null) existing.setMaterialComposition(req.getMaterialComposition());
        if (req.getCareInstructions() != null) existing.setCareInstructions(req.getCareInstructions());
        if (req.getSustainabilityMetrics() != null) existing.setSustainabilityMetrics(req.getSustainabilityMetrics());
        if (req.getRfidTag() != null) existing.setRfidTag(req.getRfidTag());
        if (req.getQrCode() != null) existing.setQrCode(req.getQrCode());
        if (req.getSku() != null) existing.setSku(req.getSku());

        Item updated = itemService.update(id, existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete item")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.noContent().build();
    }
} 