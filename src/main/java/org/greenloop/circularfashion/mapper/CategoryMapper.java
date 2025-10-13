package org.greenloop.circularfashion.mapper;

import org.greenloop.circularfashion.entity.Category;
import org.greenloop.circularfashion.entity.request.CategoryCreateRequest;
import org.greenloop.circularfashion.entity.request.CategoryUpdateRequest;
import org.greenloop.circularfashion.entity.response.CategoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    
    /**
     * Convert Category entity to CategoryResponse DTO
     */
    public CategoryResponse toResponse(Category category) {
        return toResponse(category, false, null);
    }
    
    /**
     * Convert Category entity to CategoryResponse DTO with optional subcategories
     */
    public CategoryResponse toResponse(Category category, boolean includeSubCategories, Long itemCount) {
        if (category == null) {
            return null;
        }
        
        CategoryResponse.CategoryResponseBuilder builder = CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategory() != null 
                        ? category.getParentCategory().getCategoryId() 
                        : null)
                .parentCategoryName(category.getParentCategory() != null 
                        ? category.getParentCategory().getName() 
                        : null)
                .displayOrder(category.getDisplayOrder())
                .isActive(category.getIsActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .isRootCategory(category.isRootCategory())
                .hasSubCategories(category.hasSubCategories())
                .fullPath(category.getFullPath())
                .level(category.getLevel())
                .totalItems(itemCount);
        
        // Include subcategories if requested
        if (includeSubCategories && category.getSubCategories() != null) {
            List<CategoryResponse> subCategoryResponses = category.getSubCategories().stream()
                    .map(subCat -> toResponse(subCat, false, null))
                    .collect(Collectors.toList());
            builder.subCategories(subCategoryResponses);
        }
        
        return builder.build();
    }
    
    /**
     * Convert CategoryCreateRequest to Category entity
     */
    public Category toEntity(CategoryCreateRequest request, Category parentCategory) {
        if (request == null) {
            return null;
        }
        
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .parentCategory(parentCategory)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }
    
    /**
     * Update existing Category entity with data from CategoryUpdateRequest
     */
    public void updateEntity(Category category, CategoryUpdateRequest request, Category parentCategory) {
        if (category == null || request == null) {
            return;
        }
        
        if (request.getName() != null) {
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (parentCategory != null) {
            category.setParentCategory(parentCategory);
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
    }
    
    /**
     * Convert list of Categories to list of CategoryResponse
     */
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return toResponseList(categories, false);
    }
    
    /**
     * Convert list of Categories to list of CategoryResponse with optional subcategories
     */
    public List<CategoryResponse> toResponseList(List<Category> categories, boolean includeSubCategories) {
        if (categories == null) {
            return List.of();
        }
        return categories.stream()
                .map(cat -> toResponse(cat, includeSubCategories, null))
                .collect(Collectors.toList());
    }
}









