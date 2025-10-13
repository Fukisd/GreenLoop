package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.PointTransaction;
import org.greenloop.circularfashion.entity.request.PointEarningRequest;
import org.greenloop.circularfashion.entity.request.PointsRedemptionRequest;
import org.greenloop.circularfashion.entity.response.ApiResponse;
import org.greenloop.circularfashion.entity.response.PointSummaryResponse;
import org.greenloop.circularfashion.entity.response.PointTransactionResponse;
import org.greenloop.circularfashion.service.PointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Point Management", description = "APIs for comprehensive point management system")
public class PointController {

    private final PointService pointService;

    // ==================== Point Transactions ====================
    
    @PostMapping("/earn")
    @Operation(summary = "Earn points", description = "Award points to a user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> earnPoints(
            @Valid @RequestBody PointEarningRequest request) {
        PointTransactionResponse transaction = pointService.earnPoints(request);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Points awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/redeem")
    @Operation(summary = "Redeem points", description = "Redeem points for rewards or discounts")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> redeemPoints(
            @Valid @RequestBody PointsRedemptionRequest request) {
        PointTransactionResponse transaction = pointService.redeemPoints(request);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Points redeemed successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/adjust")
    @Operation(summary = "Adjust points", description = "Manually adjust user's points")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> adjustPoints(
            @RequestParam UUID userId,
            @RequestParam Integer points,
            @RequestParam String reason) {
        PointTransactionResponse transaction = pointService.adjustPoints(userId, points, reason);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Points adjusted successfully")
                .data(transaction)
                .build());
    }

    // ==================== Point Queries ====================
    
    @GetMapping("/summary/{userId}")
    @Operation(summary = "Get point summary", description = "Get comprehensive point summary for a user")
    public ResponseEntity<ApiResponse<PointSummaryResponse>> getPointSummary(@PathVariable UUID userId) {
        PointSummaryResponse summary = pointService.getPointSummary(userId);
        return ResponseEntity.ok(ApiResponse.<PointSummaryResponse>builder()
                .success(true)
                .message("Point summary retrieved successfully")
                .data(summary)
                .build());
    }

    @GetMapping("/transactions/{userId}")
    @Operation(summary = "Get user transactions", description = "Get all point transactions for a user")
    public ResponseEntity<ApiResponse<Page<PointTransactionResponse>>> getUserTransactions(
            @PathVariable UUID userId,
            Pageable pageable) {
        Page<PointTransactionResponse> transactions = pointService.getUserTransactions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<PointTransactionResponse>>builder()
                .success(true)
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build());
    }

    @GetMapping("/transactions/{userId}/recent")
    @Operation(summary = "Get recent transactions", description = "Get recent point transactions for a user")
    public ResponseEntity<ApiResponse<List<PointTransactionResponse>>> getRecentTransactions(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<PointTransactionResponse> transactions = pointService.getRecentTransactions(userId, limit);
        return ResponseEntity.ok(ApiResponse.<List<PointTransactionResponse>>builder()
                .success(true)
                .message("Recent transactions retrieved successfully")
                .data(transactions)
                .build());
    }

    @GetMapping("/transactions/{userId}/type/{type}")
    @Operation(summary = "Get transactions by type", description = "Get point transactions by type for a user")
    public ResponseEntity<ApiResponse<Page<PointTransactionResponse>>> getTransactionsByType(
            @PathVariable UUID userId,
            @PathVariable PointTransaction.TransactionType type,
            Pageable pageable) {
        Page<PointTransactionResponse> transactions = pointService.getTransactionsByType(userId, type, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<PointTransactionResponse>>builder()
                .success(true)
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build());
    }

    @GetMapping("/transactions/{userId}/date-range")
    @Operation(summary = "Get transactions by date range", description = "Get point transactions within a date range")
    public ResponseEntity<ApiResponse<Page<PointTransactionResponse>>> getTransactionsByDateRange(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        Page<PointTransactionResponse> transactions = pointService.getTransactionsByDateRange(userId, startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<PointTransactionResponse>>builder()
                .success(true)
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build());
    }

    // ==================== Point Calculations ====================
    
    @GetMapping("/{userId}/available")
    @Operation(summary = "Get available points", description = "Get the number of available points for a user")
    public ResponseEntity<ApiResponse<Integer>> getAvailablePoints(@PathVariable UUID userId) {
        Integer points = pointService.getAvailablePoints(userId);
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message("Available points retrieved successfully")
                .data(points)
                .build());
    }

    @GetMapping("/{userId}/earned")
    @Operation(summary = "Get total earned points", description = "Get the total points earned by a user")
    public ResponseEntity<ApiResponse<Integer>> getTotalEarnedPoints(@PathVariable UUID userId) {
        Integer points = pointService.getTotalEarnedPoints(userId);
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message("Total earned points retrieved successfully")
                .data(points)
                .build());
    }

    @GetMapping("/{userId}/spent")
    @Operation(summary = "Get total spent points", description = "Get the total points spent by a user")
    public ResponseEntity<ApiResponse<Integer>> getTotalSpentPoints(@PathVariable UUID userId) {
        Integer points = pointService.getTotalSpentPoints(userId);
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message("Total spent points retrieved successfully")
                .data(points)
                .build());
    }

    @GetMapping("/{userId}/expiring")
    @Operation(summary = "Get expiring points", description = "Get points expiring within specified days")
    public ResponseEntity<ApiResponse<Integer>> getExpiringPoints(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "30") Integer days) {
        Integer points = pointService.getExpiringPoints(userId, days);
        return ResponseEntity.ok(ApiResponse.<Integer>builder()
                .success(true)
                .message("Expiring points retrieved successfully")
                .data(points)
                .build());
    }

    // ==================== Point Expiration ====================
    
    @PostMapping("/expire")
    @Operation(summary = "Expire points", description = "Manually trigger point expiration process")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> expirePoints() {
        pointService.expirePoints();
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Points expiration process completed")
                .build());
    }

    @GetMapping("/{userId}/expiring-soon")
    @Operation(summary = "Get expiring soon points", description = "Get detailed list of points expiring soon")
    public ResponseEntity<ApiResponse<List<PointTransactionResponse>>> getExpiringSoonPoints(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "7") Integer days) {
        List<PointTransactionResponse> transactions = pointService.getExpiringSoonPoints(userId, days);
        return ResponseEntity.ok(ApiResponse.<List<PointTransactionResponse>>builder()
                .success(true)
                .message("Expiring points retrieved successfully")
                .data(transactions)
                .build());
    }

    @PostMapping("/{userId}/notify-expiring")
    @Operation(summary = "Notify expiring points", description = "Send notification about expiring points")
    public ResponseEntity<ApiResponse<Void>> notifyExpiringPoints(@PathVariable UUID userId) {
        pointService.notifyExpiringPoints(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Expiring points notification sent successfully")
                .build());
    }

    // ==================== Point Statistics ====================
    
    @GetMapping("/{userId}/statistics")
    @Operation(summary = "Get point statistics", description = "Get comprehensive point statistics for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPointStatistics(@PathVariable UUID userId) {
        Map<String, Object> stats = pointService.getPointStatistics(userId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Point statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/{userId}/points-by-type")
    @Operation(summary = "Get points by transaction type", description = "Get point breakdown by transaction type")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getPointsByTransactionType(@PathVariable UUID userId) {
        Map<String, Integer> pointsByType = pointService.getPointsByTransactionType(userId);
        return ResponseEntity.ok(ApiResponse.<Map<String, Integer>>builder()
                .success(true)
                .message("Points by type retrieved successfully")
                .data(pointsByType)
                .build());
    }

    // ==================== Point Actions Based on Activities ====================
    
    @PostMapping("/award/purchase")
    @Operation(summary = "Award purchase points", description = "Award points for a purchase")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardPurchasePoints(
            @RequestParam UUID userId,
            @RequestParam UUID orderId,
            @RequestParam Double purchaseAmount) {
        PointTransactionResponse transaction = pointService.awardPurchasePoints(userId, orderId, purchaseAmount);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Purchase points awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/award/collection")
    @Operation(summary = "Award collection points", description = "Award points for recycling collection")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardCollectionPoints(
            @RequestParam UUID userId,
            @RequestParam UUID collectionRequestId) {
        PointTransactionResponse transaction = pointService.awardCollectionPoints(userId, collectionRequestId);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Collection points awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/award/review")
    @Operation(summary = "Award review points", description = "Award points for writing a review")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardReviewPoints(
            @RequestParam UUID userId,
            @RequestParam UUID itemId) {
        PointTransactionResponse transaction = pointService.awardReviewPoints(userId, itemId);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Review points awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/award/referral")
    @Operation(summary = "Award referral points", description = "Award points for referring a new user")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardReferralPoints(
            @RequestParam UUID userId,
            @RequestParam UUID referredUserId) {
        PointTransactionResponse transaction = pointService.awardReferralPoints(userId, referredUserId);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Referral points awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/award/signup")
    @Operation(summary = "Award signup bonus", description = "Award signup bonus points to a new user")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardSignupBonus(@RequestParam UUID userId) {
        PointTransactionResponse transaction = pointService.awardSignupBonus(userId);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Signup bonus awarded successfully")
                .data(transaction)
                .build());
    }

    @PostMapping("/award/daily-login")
    @Operation(summary = "Award daily login points", description = "Award daily login points to a user")
    public ResponseEntity<ApiResponse<PointTransactionResponse>> awardDailyLoginPoints(@RequestParam UUID userId) {
        PointTransactionResponse transaction = pointService.awardDailyLoginPoints(userId);
        return ResponseEntity.ok(ApiResponse.<PointTransactionResponse>builder()
                .success(true)
                .message("Daily login points awarded successfully")
                .data(transaction)
                .build());
    }

    // ==================== Point Validation ====================
    
    @GetMapping("/{userId}/has-enough")
    @Operation(summary = "Check if user has enough points", description = "Validate if user has enough points")
    public ResponseEntity<ApiResponse<Boolean>> hasEnoughPoints(
            @PathVariable UUID userId,
            @RequestParam Integer requiredPoints) {
        boolean hasEnough = pointService.hasEnoughPoints(userId, requiredPoints);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("Points validation completed")
                .data(hasEnough)
                .build());
    }

    @GetMapping("/{userId}/can-redeem")
    @Operation(summary = "Check if user can redeem points", description = "Validate if user can redeem specified points")
    public ResponseEntity<ApiResponse<Boolean>> canRedeemPoints(
            @PathVariable UUID userId,
            @RequestParam Integer points) {
        boolean canRedeem = pointService.canRedeemPoints(userId, points);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .message("Redemption validation completed")
                .data(canRedeem)
                .build());
    }
}









