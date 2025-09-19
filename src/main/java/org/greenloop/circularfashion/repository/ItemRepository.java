package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.Item;
import org.greenloop.circularfashion.enums.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    List<Item> findByOwnerUserId(Long ownerId);
    
    List<Item> findByCurrentStatus(ItemStatus status);
    
    List<Item> findByOwnerUserIdAndCurrentStatus(Long ownerId, ItemStatus status);
    
    @Query("SELECT i FROM Item i WHERE i.category.categoryId = :categoryId")
    List<Item> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Query("SELECT i FROM Item i WHERE i.brand.brandId = :brandId")
    List<Item> findByBrandId(@Param("brandId") Long brandId);
} 