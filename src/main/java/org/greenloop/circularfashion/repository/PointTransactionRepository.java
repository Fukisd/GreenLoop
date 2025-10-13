package org.greenloop.circularfashion.repository;

import org.greenloop.circularfashion.entity.PointTransaction;
import org.greenloop.circularfashion.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, UUID> {

    // Find all transactions for a specific user
    Page<PointTransaction> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    List<PointTransaction> findByUserOrderByCreatedAtDesc(User user);
    
    // Find transactions by user ID
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user.userId = :userId ORDER BY pt.createdAt DESC")
    Page<PointTransaction> findByUserId(@Param("userId") UUID userId, Pageable pageable);
    
    // Find transactions by type
    List<PointTransaction> findByUserAndTransactionType(User user, PointTransaction.TransactionType transactionType);
    
    // Find earned points
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user AND (pt.transactionType = 'EARNED_COLLECTION' OR pt.transactionType = 'EARNED_PURCHASE' OR pt.transactionType = 'EARNED_REVIEW' OR pt.transactionType = 'EARNED_REFERRAL') ORDER BY pt.createdAt DESC")
    List<PointTransaction> findEarnedPointsByUser(@Param("user") User user);
    
    // Find spent points
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user AND (pt.transactionType = 'SPENT_DISCOUNT' OR pt.transactionType = 'SPENT_PREMIUM') ORDER BY pt.createdAt DESC")
    List<PointTransaction> findSpentPointsByUser(@Param("user") User user);
    
    // Find active points (not expired)
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user AND pt.status = 'COMPLETED' AND (pt.expiresAt IS NULL OR pt.expiresAt > :currentTime)")
    List<PointTransaction> findActivePointsByUser(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
    
    // Find expiring soon points
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user AND pt.status = 'COMPLETED' AND pt.expiresAt BETWEEN :startTime AND :endTime ORDER BY pt.expiresAt ASC")
    List<PointTransaction> findExpiringSoonPointsByUser(@Param("user") User user, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // Find expired points
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.status = 'EXPIRED' OR (pt.expiresAt IS NOT NULL AND pt.expiresAt < :currentTime)")
    List<PointTransaction> findExpiredPoints(@Param("currentTime") LocalDateTime currentTime);
    
    // Calculate total earned points
    @Query("SELECT COALESCE(SUM(pt.pointsAmount), 0) FROM PointTransaction pt WHERE pt.user = :user AND (pt.transactionType = 'EARNED_COLLECTION' OR pt.transactionType = 'EARNED_PURCHASE' OR pt.transactionType = 'EARNED_REVIEW' OR pt.transactionType = 'EARNED_REFERRAL') AND pt.status = 'COMPLETED'")
    Integer calculateTotalEarnedPoints(@Param("user") User user);
    
    // Calculate total spent points
    @Query("SELECT COALESCE(SUM(pt.pointsAmount), 0) FROM PointTransaction pt WHERE pt.user = :user AND (pt.transactionType = 'SPENT_DISCOUNT' OR pt.transactionType = 'SPENT_PREMIUM') AND pt.status = 'COMPLETED'")
    Integer calculateTotalSpentPoints(@Param("user") User user);
    
    // Calculate available points (earned - spent - expired)
    @Query("SELECT COALESCE(SUM(CASE WHEN (pt.transactionType = 'EARNED_COLLECTION' OR pt.transactionType = 'EARNED_PURCHASE' OR pt.transactionType = 'EARNED_REVIEW' OR pt.transactionType = 'EARNED_REFERRAL') THEN pt.pointsAmount WHEN (pt.transactionType = 'SPENT_DISCOUNT' OR pt.transactionType = 'SPENT_PREMIUM') THEN -pt.pointsAmount ELSE 0 END), 0) " +
           "FROM PointTransaction pt WHERE pt.user = :user AND pt.status = 'COMPLETED' AND (pt.expiresAt IS NULL OR pt.expiresAt > :currentTime)")
    Integer calculateAvailablePoints(@Param("user") User user, @Param("currentTime") LocalDateTime currentTime);
    
    // Find transactions within date range
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user AND pt.createdAt BETWEEN :startDate AND :endDate ORDER BY pt.createdAt DESC")
    Page<PointTransaction> findByUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // Get transaction statistics
    @Query("SELECT pt.transactionType, COUNT(pt), SUM(pt.pointsAmount) FROM PointTransaction pt WHERE pt.user = :user AND pt.status = 'COMPLETED' GROUP BY pt.transactionType")
    List<Object[]> getTransactionStatisticsByUser(@Param("user") User user);
    
    // Find transactions by order
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.order.orderId = :orderId")
    List<PointTransaction> findByOrderId(@Param("orderId") UUID orderId);
    
    // Find recent transactions
    @Query("SELECT pt FROM PointTransaction pt WHERE pt.user = :user ORDER BY pt.createdAt DESC")
    List<PointTransaction> findRecentTransactions(@Param("user") User user, Pageable pageable);
}

