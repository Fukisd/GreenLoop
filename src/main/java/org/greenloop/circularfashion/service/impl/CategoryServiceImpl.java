package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.Category;
import org.greenloop.circularfashion.entity.request.CategoryCreateRequest;
import org.greenloop.circularfashion.entity.request.CategoryUpdateRequest;
import org.greenloop.circularfashion.entity.response.CategoryResponse;
import org.greenloop.circularfashion.exception.ResourceNotFoundException;
import org.greenloop.circularfashion.mapper.CategoryMapper;
import org.greenloop.circularfashion.repository.CategoryRepository;
import org.greenloop.circularfashion.repository.ItemRepository;
import org.greenloop.circularfashion.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        log.info("Creating category: {}", request.getName());
        
        // Check if category with same name exists
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
        }
        
        // Get parent category if specified
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = getEntityById(request.getParentCategoryId());
        }
        
        Category category = categoryMapper.toEntity(request, parentCategory);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with id: {}", savedCategory.getCategoryId());
        return categoryMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(UUID id) {
        log.debug("Fetching category by id: {}", id);
        Category category = getEntityById(id);
        Long itemCount = (long) itemRepository.findByCategoryId(id).size();
        return categoryMapper.toResponse(category, false, itemCount);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        log.debug("Fetching category by slug: {}", slug);
        Category category = getEntityBySlug(slug);
        Long itemCount = (long) itemRepository.findByCategoryId(category.getCategoryId()).size();
        return categoryMapper.toResponse(category, false, itemCount);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryUpdateRequest request) {
        log.info("Updating category: {}", id);
        
        Category category = getEntityById(id);
        
        // Check if new name conflicts with existing category
        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Category with name '" + request.getName() + "' already exists");
            }
        }
        
        // Get parent category if specified
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = getEntityById(request.getParentCategoryId());
            
            // Prevent circular references
            if (parentCategory.getCategoryId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
        }
        
        categoryMapper.updateEntity(category, request, parentCategory);
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Category updated successfully: {}", id);
        return categoryMapper.toResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id) {
        log.info("Soft deleting category: {}", id);
        
        Category category = getEntityById(id);
        
        // Soft delete: Set isActive to false
        category.setIsActive(false);
        
        // Also soft delete all subcategories recursively
        if (category.hasSubCategories()) {
            category.getSubCategories().forEach(subCategory -> {
                subCategory.setIsActive(false);
                categoryRepository.save(subCategory);
            });
        }
        
        categoryRepository.save(category);
        log.info("Category soft deleted successfully (set to inactive): {}", id);
    }

    @Override
    public void hardDeleteCategory(UUID id) {
        log.info("Hard deleting category: {}", id);
        
        Category category = getEntityById(id);
        
        // Check if category has items
        List<?> items = itemRepository.findByCategoryId(id);
        if (!items.isEmpty()) {
            throw new IllegalStateException("Cannot permanently delete category with " + items.size() + " items. Please reassign or delete items first.");
        }
        
        // Check if category has subcategories
        if (category.hasSubCategories()) {
            throw new IllegalStateException("Cannot permanently delete category with subcategories. Please delete subcategories first.");
        }
        
        categoryRepository.delete(category);
        log.info("Category permanently deleted: {}", id);
    }

    @Override
    public CategoryResponse restoreCategory(UUID id) {
        log.info("Restoring soft-deleted category: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        category.setIsActive(true);
        Category restoredCategory = categoryRepository.save(category);
        
        log.info("Category restored successfully: {}", id);
        return categoryMapper.toResponse(restoredCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getEntityById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getEntityBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "slug", slug));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        log.debug("Fetching all categories with pagination");
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllActiveCategories() {
        log.debug("Fetching all active categories");
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllInactiveCategories() {
        log.debug("Fetching all inactive (soft-deleted) categories");
        List<Category> categories = categoryRepository.findByIsActiveFalse();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getRootCategories() {
        log.debug("Fetching root categories");
        List<Category> categories = categoryRepository.findByParentCategoryIsNullAndIsActiveTrue();
        return categoryMapper.toResponseList(categories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(UUID parentCategoryId) {
        log.debug("Fetching subcategories for parent: {}", parentCategoryId);
        Category parentCategory = getEntityById(parentCategoryId);
        List<Category> subCategories = categoryRepository.findByParentCategoryAndIsActiveTrue(parentCategory);
        return categoryMapper.toResponseList(subCategories);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        log.debug("Fetching category tree");
        List<Category> rootCategories = categoryRepository.findByParentCategoryIsNullAndIsActiveTrue();
        return categoryMapper.toResponseList(rootCategories, true);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryWithSubCategories(UUID id) {
        log.debug("Fetching category with subcategories: {}", id);
        Category category = getEntityById(id);
        Long itemCount = (long) itemRepository.findByCategoryId(id).size();
        return categoryMapper.toResponse(category, true, itemCount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> searchCategories(String keyword, Pageable pageable) {
        log.debug("Searching categories with keyword: {}", keyword);
        Page<Category> categories = categoryRepository.searchCategories(keyword, pageable);
        return categories.map(categoryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCategories() {
        return categoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long countRootCategories() {
        return categoryRepository.countByParentCategoryIsNull();
    }
}

