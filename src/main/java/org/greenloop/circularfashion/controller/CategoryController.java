package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.request.CategoryCreateRequest;
import org.greenloop.circularfashion.entity.request.CategoryUpdateRequest;
import org.greenloop.circularfashion.entity.response.CategoryResponse;
import org.greenloop.circularfashion.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    // ==================== CRUD Operations ====================

    @PostMapping
    @Operation(summary = "Create new category", description = "Create a new category in the system")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        log.info("Creating category: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Retrieve detailed information about a specific category")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        log.debug("Fetching category: {}", id);
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get category by slug", description = "Retrieve category information by slug")
    public ResponseEntity<CategoryResponse> getCategoryBySlug(@PathVariable String slug) {
        log.debug("Fetching category by slug: {}", slug);
        CategoryResponse response = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category's information")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        
        log.info("Updating category: {}", id);
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category (soft)", 
               description = "Soft delete a category and all subcategories by setting isActive=false")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        log.info("Soft deleting category: {}", id);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Delete category permanently", 
               description = "Permanently delete a category from the database (admin only)")
    public ResponseEntity<Void> hardDeleteCategory(@PathVariable UUID id) {
        log.warn("Hard deleting category: {}", id);
        categoryService.hardDeleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Restore category", description = "Restore a soft-deleted category by setting isActive=true")
    public ResponseEntity<CategoryResponse> restoreCategory(@PathVariable UUID id) {
        log.info("Restoring category: {}", id);
        CategoryResponse response = categoryService.restoreCategory(id);
        return ResponseEntity.ok(response);
    }

    // ==================== Listing & Pagination ====================

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieve all categories with pagination")
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Fetching all categories");
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active categories", description = "Retrieve all active categories")
    public ResponseEntity<List<CategoryResponse>> getAllActiveCategories() {
        log.debug("Fetching active categories");
        List<CategoryResponse> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/inactive")
    @Operation(summary = "Get inactive categories", description = "Retrieve all soft-deleted categories (admin only)")
    public ResponseEntity<List<CategoryResponse>> getAllInactiveCategories() {
        log.debug("Fetching inactive categories");
        List<CategoryResponse> categories = categoryService.getAllInactiveCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories", description = "Retrieve all top-level categories (no parent)")
    public ResponseEntity<List<CategoryResponse>> getRootCategories() {
        log.debug("Fetching root categories");
        List<CategoryResponse> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "Get subcategories", description = "Get all subcategories of a parent category")
    public ResponseEntity<List<CategoryResponse>> getSubCategories(@PathVariable UUID id) {
        log.debug("Fetching subcategories for parent: {}", id);
        List<CategoryResponse> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(subCategories);
    }

    // ==================== Tree Structure ====================

    @GetMapping("/tree")
    @Operation(summary = "Get category tree", description = "Get the complete category hierarchy tree")
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() {
        log.debug("Fetching category tree");
        List<CategoryResponse> tree = categoryService.getCategoryTree();
        return ResponseEntity.ok(tree);
    }

    @GetMapping("/{id}/with-subcategories")
    @Operation(summary = "Get category with subcategories", 
               description = "Get a category with all its subcategories included")
    public ResponseEntity<CategoryResponse> getCategoryWithSubCategories(@PathVariable UUID id) {
        log.debug("Fetching category with subcategories: {}", id);
        CategoryResponse response = categoryService.getCategoryWithSubCategories(id);
        return ResponseEntity.ok(response);
    }

    // ==================== Search ====================

    @GetMapping("/search")
    @Operation(summary = "Search categories", description = "Search categories by keyword in name and description")
    public ResponseEntity<Page<CategoryResponse>> searchCategories(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("Searching categories with keyword: {}", keyword);
        Page<CategoryResponse> categories = categoryService.searchCategories(keyword, pageable);
        return ResponseEntity.ok(categories);
    }

    // ==================== Statistics ====================

    @GetMapping("/statistics")
    @Operation(summary = "Get category statistics", description = "Get various statistics about categories")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.debug("Fetching category statistics");
        
        Long totalCategories = categoryService.countCategories();
        Long rootCategories = categoryService.countRootCategories();
        
        return ResponseEntity.ok(Map.of(
                "totalCategories", totalCategories,
                "rootCategories", rootCategories,
                "subCategories", totalCategories - rootCategories
        ));
    }
}

