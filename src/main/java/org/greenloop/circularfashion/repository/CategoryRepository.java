package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    // Basic queries
    Optional<Category> findByName(String name);
    Optional<Category> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    
    // Active/Inactive categories
    List<Category> findByIsActiveTrue();
    Page<Category> findByIsActiveTrueOrderByDisplayOrderAsc(Pageable pageable);
    List<Category> findByIsActiveFalse();
    Page<Category> findByIsActiveFalse(Pageable pageable);
    
    // Parent/child relationships
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategoryIsNullAndIsActiveTrue();
    List<Category> findByParentCategory(Category parentCategory);
    List<Category> findByParentCategoryAndIsActiveTrue(Category parentCategory);
    
    @Query("SELECT c FROM Category c WHERE c.parentCategory.categoryId = :parentId")
    List<Category> findByParentCategoryId(@Param("parentId") UUID parentId);
    
    // Search
    @Query("SELECT c FROM Category c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Category> searchCategories(@Param("keyword") String keyword, Pageable pageable);
    
    // Count queries
    Long countByParentCategoryIsNull();
} 