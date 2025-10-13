package org.greenloop.circularfashion.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.greenloop.circularfashion.entity.PointEarningRule;
import org.greenloop.circularfashion.entity.response.ApiResponse;
import org.greenloop.circularfashion.entity.response.PointEarningRuleResponse;
import org.greenloop.circularfashion.repository.PointEarningRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/point-rules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Point Earning Rules", description = "APIs for managing point earning rules")
public class PointEarningRuleController {

    private final PointEarningRuleRepository pointEarningRuleRepository;

    @GetMapping
    @Operation(summary = "Get all rules", description = "Get all point earning rules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PointEarningRuleResponse>>> getAllRules() {
        List<PointEarningRule> rules = pointEarningRuleRepository.findAll();
        List<PointEarningRuleResponse> responses = rules.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.<List<PointEarningRuleResponse>>builder()
                .success(true)
                .message("Rules retrieved successfully")
                .data(responses)
                .build());
    }

    @GetMapping("/active")
    @Operation(summary = "Get active rule", description = "Get the currently active point earning rule")
    public ResponseEntity<ApiResponse<PointEarningRuleResponse>> getActiveRule() {
        PointEarningRule rule = pointEarningRuleRepository.findActiveRule()
                .orElse(null);
        
        return ResponseEntity.ok(ApiResponse.<PointEarningRuleResponse>builder()
                .success(true)
                .message("Active rule retrieved successfully")
                .data(rule != null ? convertToResponse(rule) : null)
                .build());
    }

    @GetMapping("/{ruleId}")
    @Operation(summary = "Get rule by ID", description = "Get a specific point earning rule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PointEarningRuleResponse>> getRuleById(@PathVariable UUID ruleId) {
        PointEarningRule rule = pointEarningRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        return ResponseEntity.ok(ApiResponse.<PointEarningRuleResponse>builder()
                .success(true)
                .message("Rule retrieved successfully")
                .data(convertToResponse(rule))
                .build());
    }

    @PostMapping
    @Operation(summary = "Create rule", description = "Create a new point earning rule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PointEarningRuleResponse>> createRule(
            @Valid @RequestBody PointEarningRule rule) {
        PointEarningRule savedRule = pointEarningRuleRepository.save(rule);
        
        return ResponseEntity.ok(ApiResponse.<PointEarningRuleResponse>builder()
                .success(true)
                .message("Rule created successfully")
                .data(convertToResponse(savedRule))
                .build());
    }

    @PutMapping("/{ruleId}")
    @Operation(summary = "Update rule", description = "Update an existing point earning rule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PointEarningRuleResponse>> updateRule(
            @PathVariable UUID ruleId,
            @Valid @RequestBody PointEarningRule rule) {
        PointEarningRule existingRule = pointEarningRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        rule.setRuleId(ruleId);
        PointEarningRule updatedRule = pointEarningRuleRepository.save(rule);
        
        return ResponseEntity.ok(ApiResponse.<PointEarningRuleResponse>builder()
                .success(true)
                .message("Rule updated successfully")
                .data(convertToResponse(updatedRule))
                .build());
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "Delete rule", description = "Delete a point earning rule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteRule(@PathVariable UUID ruleId) {
        pointEarningRuleRepository.deleteById(ruleId);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Rule deleted successfully")
                .build());
    }

    @PatchMapping("/{ruleId}/activate")
    @Operation(summary = "Activate rule", description = "Activate a point earning rule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> activateRule(@PathVariable UUID ruleId) {
        // Deactivate all other rules first
        List<PointEarningRule> allRules = pointEarningRuleRepository.findAll();
        allRules.forEach(r -> {
            r.setIsActive(false);
            pointEarningRuleRepository.save(r);
        });
        
        // Activate the specified rule
        PointEarningRule rule = pointEarningRuleRepository.findById(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        rule.setIsActive(true);
        pointEarningRuleRepository.save(rule);
        
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Rule activated successfully")
                .build());
    }

    private PointEarningRuleResponse convertToResponse(PointEarningRule rule) {
        return PointEarningRuleResponse.builder()
                .ruleId(rule.getRuleId())
                .ruleName(rule.getRuleName())
                .description(rule.getDescription())
                .pointsPerPurchase(rule.getPointsPerPurchase())
                .pointsPerCollection(rule.getPointsPerCollection())
                .pointsPerReview(rule.getPointsPerReview())
                .pointsPerReferral(rule.getPointsPerReferral())
                .signupBonus(rule.getSignupBonus())
                .dailyLoginPoints(rule.getDailyLoginPoints())
                .pointValueInCurrency(rule.getPointValueInCurrency())
                .minimumRedemptionPoints(rule.getMinimumRedemptionPoints())
                .pointsExpireInDays(rule.getPointsExpireInDays())
                .expirationEnabled(rule.getExpirationEnabled())
                .eventMultiplier(rule.getEventMultiplier())
                .eventStartDate(rule.getEventStartDate())
                .eventEndDate(rule.getEventEndDate())
                .isActive(rule.getIsActive())
                .isEventActive(rule.isEventActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}









