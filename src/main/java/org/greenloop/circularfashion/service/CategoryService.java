package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.Category;
import org.greenloop.circularfashion.entity.request.CategoryCreateRequest;
import org.greenloop.circularfashion.entity.request.CategoryUpdateRequest;
import org.greenloop.circularfashion.entity.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    
    // CRUD operations
    CategoryResponse createCategory(CategoryCreateRequest request);
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse getCategoryBySlug(String slug);
    CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request);
    void deleteCategory(UUID id); // Soft delete (set isActive = false)
    void hardDeleteCategory(UUID id); // Permanent delete
    CategoryResponse restoreCategory(UUID id); // Restore soft-deleted category
    
    // Get operations returning entities (for internal use)
    Category getEntityById(UUID id);
    Category getEntityBySlug(String slug);
    
    // Listing and pagination
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    List<CategoryResponse> getAllActiveCategories();
    List<CategoryResponse> getAllInactiveCategories(); // View soft-deleted categories
    List<CategoryResponse> getRootCategories();
    List<CategoryResponse> getSubCategories(UUID parentCategoryId);
    
    // Tree structure
    List<CategoryResponse> getCategoryTree();
    CategoryResponse getCategoryWithSubCategories(UUID id);
    
    // Search
    Page<CategoryResponse> searchCategories(String keyword, Pageable pageable);
    
    // Statistics
    Long countCategories();
    Long countRootCategories();
}

