package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

    // Basic finding methods
    Optional<Item> findByItemCode(String itemCode);
    
    // Owner queries
    List<Item> findByCurrentOwner(User currentOwner);
    List<Item> findByOriginalOwner(User originalOwner);
    Page<Item> findByCurrentOwnerOrderByCreatedAtDesc(User currentOwner, Pageable pageable);

    // Status queries
    List<Item> findByItemStatus(Item.ItemStatus itemStatus);
    List<Item> findByItemStatusIn(List<Item.ItemStatus> statuses);
    
    @Query("SELECT i FROM Item i WHERE i.itemStatus = :status AND i.isVerified = true")
    Page<Item> findByStatusAndVerified(@Param("status") Item.ItemStatus status, Pageable pageable);

    // Category queries
    @Query("SELECT i FROM Item i WHERE i.category.categoryId = :categoryId")
    List<Item> findByCategoryId(@Param("categoryId") UUID categoryId);

    @Query("SELECT i FROM Item i WHERE i.category.categoryId = :categoryId AND i.itemStatus = :status")
    Page<Item> findByCategoryAndStatus(@Param("categoryId") UUID categoryId, 
                                       @Param("status") Item.ItemStatus status, 
                                       Pageable pageable);

    // Brand queries
    @Query("SELECT i FROM Item i WHERE i.brand.brandId = :brandId")
    List<Item> findByBrandId(@Param("brandId") UUID brandId);

    // Condition queries
    @Query("SELECT i FROM Item i WHERE i.conditionScore >= :minScore")
    List<Item> findByMinConditionScore(@Param("minScore") BigDecimal minScore);

    @Query("SELECT i FROM Item i WHERE i.conditionScore BETWEEN :minScore AND :maxScore")
    Page<Item> findByConditionScoreBetween(@Param("minScore") BigDecimal minScore, 
                                           @Param("maxScore") BigDecimal maxScore, 
                                           Pageable pageable);

    // Search and filter queries
    @Query("SELECT i FROM Item i WHERE " +
           "LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Item> searchItems(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT i FROM Item i WHERE " +
           "i.category.categoryId = :categoryId AND " +
           "i.itemStatus IN :statuses AND " +
           "i.conditionScore >= :minCondition")
    Page<Item> findWithFilters(@Param("categoryId") UUID categoryId,
                               @Param("statuses") List<Item.ItemStatus> statuses,
                               @Param("minCondition") BigDecimal minCondition,
                               Pageable pageable);

    // Marketplace ready items
    @Query("SELECT i FROM Item i WHERE i.itemStatus IN ('READY_FOR_SALE', 'VALUED') AND i.isVerified = true")
    Page<Item> findMarketplaceReadyItems(Pageable pageable);

    // Collection queries
    @Query("SELECT i FROM Item i WHERE i.itemStatus IN ('SUBMITTED', 'PENDING_COLLECTION', 'COLLECTED')")
    List<Item> findItemsInCollectionProcess();

    @Query("SELECT i FROM Item i WHERE i.itemStatus = 'COLLECTED' AND i.verifiedBy IS NULL")
    List<Item> findItemsAwaitingValuation();

    // Sustainability queries
    @Query("SELECT i FROM Item i WHERE i.carbonFootprintKg IS NOT NULL OR i.waterSavedLiters IS NOT NULL OR i.energySavedKwh IS NOT NULL")
    List<Item> findSustainableItems();

    // Statistics queries
    @Query("SELECT COUNT(i) FROM Item i WHERE i.createdAt >= :startDate")
    Long countItemsCreatedAfter(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT i.itemStatus, COUNT(i) FROM Item i GROUP BY i.itemStatus")
    List<Object[]> countItemsByStatus();

    @Query("SELECT c.name, COUNT(i) FROM Item i JOIN i.category c GROUP BY c.name")
    List<Object[]> countItemsByCategory();

    @Query("SELECT AVG(i.conditionScore) FROM Item i WHERE i.conditionScore IS NOT NULL")
    BigDecimal getAverageConditionScore();

    // Recent items
    @Query("SELECT i FROM Item i WHERE i.itemStatus = :status ORDER BY i.createdAt DESC")
    List<Item> findRecentItemsByStatus(@Param("status") Item.ItemStatus status, Pageable pageable);

    // Value queries
    @Query("SELECT i FROM Item i WHERE i.currentEstimatedValue BETWEEN :minValue AND :maxValue")
    Page<Item> findByValueRange(@Param("minValue") BigDecimal minValue, 
                                @Param("maxValue") BigDecimal maxValue, 
                                Pageable pageable);

    // Size and color queries
    List<Item> findBySize(String size);
    List<Item> findByColor(String color);
    List<Item> findBySizeAndColor(String size, String color);

    // Verification queries
    List<Item> findByIsVerifiedTrue();
    List<Item> findByIsVerifiedFalse();
    
    @Query("SELECT i FROM Item i WHERE i.isVerified = false AND i.itemStatus = 'COLLECTED'")
    List<Item> findUnverifiedCollectedItems();
} 