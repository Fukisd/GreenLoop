package org.greenloop.circularfashion.service;

import org.greenloop.circularfashion.entity.PointTransaction;
import org.greenloop.circularfashion.entity.request.PointEarningRequest;
import org.greenloop.circularfashion.entity.request.PointsRedemptionRequest;
import org.greenloop.circularfashion.entity.response.PointSummaryResponse;
import org.greenloop.circularfashion.entity.response.PointTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PointService {

    // Point transactions
    PointTransactionResponse earnPoints(PointEarningRequest request);
    
    PointTransactionResponse redeemPoints(PointsRedemptionRequest request);
    
    PointTransactionResponse adjustPoints(UUID userId, Integer points, String reason);
    
    // Point queries
    PointSummaryResponse getPointSummary(UUID userId);
    
    Page<PointTransactionResponse> getUserTransactions(UUID userId, Pageable pageable);
    
    List<PointTransactionResponse> getRecentTransactions(UUID userId, int limit);
    
    Page<PointTransactionResponse> getTransactionsByType(UUID userId, PointTransaction.TransactionType type, Pageable pageable);
    
    Page<PointTransactionResponse> getTransactionsByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Point calculations
    Integer getAvailablePoints(UUID userId);
    
    Integer getTotalEarnedPoints(UUID userId);
    
    Integer getTotalSpentPoints(UUID userId);
    
    Integer getExpiringPoints(UUID userId, Integer days);
    
    // Point expiration
    void expirePoints();
    
    List<PointTransactionResponse> getExpiringSoonPoints(UUID userId, Integer days);
    
    void notifyExpiringPoints(UUID userId);
    
    // Point statistics
    Map<String, Object> getPointStatistics(UUID userId);
    
    Map<String, Integer> getPointsByTransactionType(UUID userId);
    
    // Point actions based on user activities
    PointTransactionResponse awardPurchasePoints(UUID userId, UUID orderId, Double purchaseAmount);
    
    PointTransactionResponse awardCollectionPoints(UUID userId, UUID collectionRequestId);
    
    PointTransactionResponse awardReviewPoints(UUID userId, UUID itemId);
    
    PointTransactionResponse awardReferralPoints(UUID userId, UUID referredUserId);
    
    PointTransactionResponse awardSignupBonus(UUID userId);
    
    PointTransactionResponse awardDailyLoginPoints(UUID userId);
    
    // Point validation
    boolean hasEnoughPoints(UUID userId, Integer requiredPoints);
    
    boolean canRedeemPoints(UUID userId, Integer points);
    
    // Helper methods
    PointTransactionResponse convertToResponse(PointTransaction transaction);
    
    PointTransaction convertToEntity(PointTransactionResponse response);
}









