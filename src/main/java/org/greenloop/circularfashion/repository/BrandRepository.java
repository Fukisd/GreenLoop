package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    
    // Basic queries
    Optional<Brand> findByName(String name);
    Optional<Brand> findBySlug(String slug);
    boolean existsByName(String name);
    boolean existsBySlug(String slug);
    
    // Active/Inactive brands
    List<Brand> findByIsActiveTrue();
    Page<Brand> findByIsActiveTrue(Pageable pageable);
    List<Brand> findByIsActiveFalse();
    Page<Brand> findByIsActiveFalse(Pageable pageable);
    
    // Verified and partner brands
    List<Brand> findByIsVerifiedTrue();
    List<Brand> findByIsPartnerTrue();
    
    // Sustainable brands
    @Query("SELECT b FROM Brand b WHERE b.sustainabilityRating >= :minRating")
    List<Brand> findBySustainabilityRatingGreaterThanEqual(@Param("minRating") BigDecimal minRating);
    
    // Search
    @Query("SELECT b FROM Brand b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Brand> searchBrands(@Param("keyword") String keyword, Pageable pageable);
    
    // Count queries
    Long countByIsVerifiedTrue();
    Long countByIsPartnerTrue();
} 