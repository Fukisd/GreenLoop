package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    
    private UUID categoryId;
    private String name;
    private String slug;
    private String description;
    
    // Parent category info
    private UUID parentCategoryId;
    private String parentCategoryName;
    
    // Subcategories
    private List<CategoryResponse> subCategories;
    
    // Settings
    private Integer displayOrder;
    private Boolean isActive;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Computed fields
    private Boolean isRootCategory;
    private Boolean hasSubCategories;
    private String fullPath;
    private Integer level;
    
    // Statistics
    private Long totalItems;
}









