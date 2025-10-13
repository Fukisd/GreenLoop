package org.greenloop.circularfashion.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.*;
import org.greenloop.circularfashion.entity.request.PointEarningRequest;
import org.greenloop.circularfashion.entity.request.PointsRedemptionRequest;
import org.greenloop.circularfashion.entity.response.PointSummaryResponse;
import org.greenloop.circularfashion.entity.response.PointTransactionResponse;
import org.greenloop.circularfashion.exception.ResourceNotFoundException;
import org.greenloop.circularfashion.repository.*;
import org.greenloop.circularfashion.service.EmailService;
import org.greenloop.circularfashion.service.PointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final CollectionRequestRepository collectionRequestRepository;
    private final PointEarningRuleRepository pointEarningRuleRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public PointTransactionResponse earnPoints(PointEarningRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Integer currentBalance = user.getSustainabilityPoints() != null ? user.getSustainabilityPoints() : 0;
        Integer newBalance = currentBalance + request.getPointsAmount();

        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .transactionType(PointTransaction.TransactionType.valueOf(request.getTransactionType()))
                .pointsAmount(request.getPointsAmount())
                .description(request.getDescription())
                .balanceBefore(currentBalance)
                .balanceAfter(newBalance)
                .status(PointTransaction.Status.COMPLETED)
                .build();

        // Set expiration date based on earning rule
        PointEarningRule rule = getActiveEarningRule();
        if (rule != null && rule.getExpirationEnabled()) {
            transaction.setExpiresAt(rule.calculateExpirationDate());
        }

        // Set related entities
        if (request.getOrderId() != null) {
            orderRepository.findById(request.getOrderId()).ifPresent(transaction::setOrder);
        }
        if (request.getItemId() != null) {
            itemRepository.findById(request.getItemId()).ifPresent(transaction::setItem);
        }
        if (request.getCollectionRequestId() != null) {
            collectionRequestRepository.findById(request.getCollectionRequestId()).ifPresent(transaction::setCollectionRequest);
        }

        PointTransaction savedTransaction = pointTransactionRepository.save(transaction);

        // Update user's points
        user.addSustainabilityPoints(request.getPointsAmount());
        userRepository.save(user);

        log.info("User {} earned {} points. New balance: {}", request.getUserId(), request.getPointsAmount(), newBalance);

        return convertToResponse(savedTransaction);
    }

    @Override
    @Transactional
    public PointTransactionResponse redeemPoints(PointsRedemptionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Integer currentBalance = user.getSustainabilityPoints() != null ? user.getSustainabilityPoints() : 0;

        if (currentBalance < request.getPointsToRedeem()) {
            throw new IllegalArgumentException("Insufficient points. Available: " + currentBalance + ", Required: " + request.getPointsToRedeem());
        }

        // Check minimum redemption points
        PointEarningRule rule = getActiveEarningRule();
        if (rule != null && request.getPointsToRedeem() < rule.getMinimumRedemptionPoints()) {
            throw new IllegalArgumentException("Minimum redemption points is " + rule.getMinimumRedemptionPoints());
        }

        Integer newBalance = currentBalance - request.getPointsToRedeem();

        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .transactionType(PointTransaction.TransactionType.SPENT_DISCOUNT)
                .pointsAmount(request.getPointsToRedeem())
                .description(request.getDescription() != null ? request.getDescription() : "Points redeemed for " + request.getRedemptionType())
                .balanceBefore(currentBalance)
                .balanceAfter(newBalance)
                .status(PointTransaction.Status.COMPLETED)
                .build();

        if (request.getOrderId() != null) {
            orderRepository.findById(request.getOrderId()).ifPresent(transaction::setOrder);
        }

        PointTransaction savedTransaction = pointTransactionRepository.save(transaction);

        // Update user's points
        user.setSustainabilityPoints(newBalance);
        userRepository.save(user);

        log.info("User {} redeemed {} points. New balance: {}", request.getUserId(), request.getPointsToRedeem(), newBalance);

        return convertToResponse(savedTransaction);
    }

    @Override
    @Transactional
    public PointTransactionResponse adjustPoints(UUID userId, Integer points, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Integer currentBalance = user.getSustainabilityPoints() != null ? user.getSustainabilityPoints() : 0;
        Integer newBalance = currentBalance + points;

        if (newBalance < 0) {
            throw new IllegalArgumentException("Adjustment would result in negative balance");
        }

        PointTransaction transaction = PointTransaction.builder()
                .user(user)
                .transactionType(PointTransaction.TransactionType.ADJUSTMENT)
                .pointsAmount(Math.abs(points))
                .description(reason)
                .balanceBefore(currentBalance)
                .balanceAfter(newBalance)
                .status(PointTransaction.Status.COMPLETED)
                .build();

        PointTransaction savedTransaction = pointTransactionRepository.save(transaction);

        // Update user's points
        user.setSustainabilityPoints(newBalance);
        userRepository.save(user);

        log.info("User {} points adjusted by {}. Reason: {}. New balance: {}", userId, points, reason, newBalance);

        return convertToResponse(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PointSummaryResponse getPointSummary(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Integer totalEarned = getTotalEarnedPoints(userId);
        Integer totalSpent = getTotalSpentPoints(userId);
        Integer available = getAvailablePoints(userId);
        Integer expiring = getExpiringPoints(userId, 30);

        Map<String, Integer> pointsByType = getPointsByTransactionType(userId);

        return PointSummaryResponse.builder()
                .totalEarnedPoints(totalEarned)
                .totalSpentPoints(totalSpent)
                .availablePoints(available)
                .expiringPoints(expiring)
                .expiringInDays(30)
                .pointsByType(pointsByType)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointTransactionResponse> getUserTransactions(UUID userId, Pageable pageable) {
        return pointTransactionRepository.findByUserId(userId, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointTransactionResponse> getRecentTransactions(UUID userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<PointTransaction> transactions = pointTransactionRepository.findRecentTransactions(
                user, PageRequest.of(0, limit));

        return transactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointTransactionResponse> getTransactionsByType(UUID userId, PointTransaction.TransactionType type, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // For now, return empty page - can be enhanced with pagination in repository
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointTransactionResponse> getTransactionsByDateRange(UUID userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return pointTransactionRepository.findByUserAndDateRange(user, startDate, endDate, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailablePoints(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return pointTransactionRepository.calculateAvailablePoints(user, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalEarnedPoints(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return pointTransactionRepository.calculateTotalEarnedPoints(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalSpentPoints(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return pointTransactionRepository.calculateTotalSpentPoints(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getExpiringPoints(UUID userId, Integer days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);

        List<PointTransaction> expiringTransactions = pointTransactionRepository
                .findExpiringSoonPointsByUser(user, now, futureDate);

        return expiringTransactions.stream()
                .mapToInt(PointTransaction::getPointsAmount)
                .sum();
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2 AM
    public void expirePoints() {
        LocalDateTime now = LocalDateTime.now();
        List<PointTransaction> expiredTransactions = pointTransactionRepository.findExpiredPoints(now);

        for (PointTransaction transaction : expiredTransactions) {
            if (transaction.getStatus() != PointTransaction.Status.EXPIRED) {
                transaction.setStatus(PointTransaction.Status.EXPIRED);
                pointTransactionRepository.save(transaction);

                // Update user's balance
                User user = transaction.getUser();
                Integer currentBalance = user.getSustainabilityPoints() != null ? user.getSustainabilityPoints() : 0;
                user.setSustainabilityPoints(Math.max(0, currentBalance - transaction.getPointsAmount()));
                userRepository.save(user);

                log.info("Expired {} points for user {}", transaction.getPointsAmount(), user.getUserId());
            }
        }

        log.info("Expired {} point transactions", expiredTransactions.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PointTransactionResponse> getExpiringSoonPoints(UUID userId, Integer days) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);

        return pointTransactionRepository.findExpiringSoonPointsByUser(user, now, futureDate)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void notifyExpiringPoints(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<PointTransactionResponse> expiringPoints = getExpiringSoonPoints(userId, 7);

        if (!expiringPoints.isEmpty()) {
            Integer totalExpiring = expiringPoints.stream()
                    .mapToInt(PointTransactionResponse::getPointsAmount)
                    .sum();

            // Send notification email
            emailService.sendPointsExpiryNotification(user.getEmail(), totalExpiring, 7);
            log.info("Sent points expiry notification to user {}: {} points expiring in 7 days", userId, totalExpiring);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getPointStatistics(UUID userId) {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("userId", userId);
        stats.put("totalEarned", getTotalEarnedPoints(userId));
        stats.put("totalSpent", getTotalSpentPoints(userId));
        stats.put("available", getAvailablePoints(userId));
        stats.put("expiring7Days", getExpiringPoints(userId, 7));
        stats.put("expiring30Days", getExpiringPoints(userId, 30));
        stats.put("pointsByType", getPointsByTransactionType(userId));

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Integer> getPointsByTransactionType(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Object[]> statistics = pointTransactionRepository.getTransactionStatisticsByUser(user);

        Map<String, Integer> result = new HashMap<>();
        for (Object[] stat : statistics) {
            PointTransaction.TransactionType type = (PointTransaction.TransactionType) stat[0];
            Long sum = (Long) stat[2];
            result.put(type.name(), sum.intValue());
        }

        return result;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardPurchasePoints(UUID userId, UUID orderId, Double purchaseAmount) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForPurchase(purchaseAmount) : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_PURCHASE")
                    .pointsAmount(points)
                    .description("Points earned from purchase")
                    .orderId(orderId)
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardCollectionPoints(UUID userId, UUID collectionRequestId) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForAction("COLLECTION") : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_COLLECTION")
                    .pointsAmount(points)
                    .description("Points earned from recycling collection")
                    .collectionRequestId(collectionRequestId)
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardReviewPoints(UUID userId, UUID itemId) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForAction("REVIEW") : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_REVIEW")
                    .pointsAmount(points)
                    .description("Points earned from writing a review")
                    .itemId(itemId)
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardReferralPoints(UUID userId, UUID referredUserId) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForAction("REFERRAL") : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_REFERRAL")
                    .pointsAmount(points)
                    .description("Points earned from referring a new user")
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardSignupBonus(UUID userId) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForAction("SIGNUP") : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_REFERRAL")
                    .pointsAmount(points)
                    .description("Welcome bonus points")
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    @Transactional
    public PointTransactionResponse awardDailyLoginPoints(UUID userId) {
        PointEarningRule rule = getActiveEarningRule();
        Integer points = rule != null ? rule.calculatePointsForAction("DAILY_LOGIN") : 0;

        if (points > 0) {
            PointEarningRequest request = PointEarningRequest.builder()
                    .userId(userId)
                    .transactionType("EARNED_REFERRAL")
                    .pointsAmount(points)
                    .description("Daily login bonus")
                    .build();

            return earnPoints(request);
        }

        return null;
    }

    @Override
    public boolean hasEnoughPoints(UUID userId, Integer requiredPoints) {
        Integer availablePoints = getAvailablePoints(userId);
        return availablePoints >= requiredPoints;
    }

    @Override
    public boolean canRedeemPoints(UUID userId, Integer points) {
        if (!hasEnoughPoints(userId, points)) {
            return false;
        }

        PointEarningRule rule = getActiveEarningRule();
        if (rule != null && points < rule.getMinimumRedemptionPoints()) {
            return false;
        }

        return true;
    }

    @Override
    public PointTransactionResponse convertToResponse(PointTransaction transaction) {
        return PointTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .userId(transaction.getUser().getUserId())
                .userName(transaction.getUser().getFullName())
                .transactionType(transaction.getTransactionType().name())
                .pointsAmount(transaction.getPointsAmount())
                .description(transaction.getDescription())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .expiresAt(transaction.getExpiresAt())
                .status(transaction.getStatus().name())
                .createdAt(transaction.getCreatedAt())
                .orderId(transaction.getOrder() != null ? transaction.getOrder().getOrderId() : null)
                .itemId(transaction.getItem() != null ? transaction.getItem().getItemId() : null)
                .collectionRequestId(transaction.getCollectionRequest() != null ? transaction.getCollectionRequest().getRequestId() : null)
                .build();
    }

    @Override
    public PointTransaction convertToEntity(PointTransactionResponse response) {
        User user = userRepository.findById(response.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return PointTransaction.builder()
                .transactionId(response.getTransactionId())
                .user(user)
                .transactionType(PointTransaction.TransactionType.valueOf(response.getTransactionType()))
                .pointsAmount(response.getPointsAmount())
                .description(response.getDescription())
                .balanceBefore(response.getBalanceBefore())
                .balanceAfter(response.getBalanceAfter())
                .expiresAt(response.getExpiresAt())
                .status(PointTransaction.Status.valueOf(response.getStatus()))
                .createdAt(response.getCreatedAt())
                .build();
    }

    // Helper method to get active earning rule
    private PointEarningRule getActiveEarningRule() {
        return pointEarningRuleRepository.findActiveRule().orElse(null);
    }
}

