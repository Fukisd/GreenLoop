package org.greenloop.circularfashion.entity.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointEarningRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Transaction type is required")
    private String transactionType; // EARNED_COLLECTION, EARNED_PURCHASE, etc.

    @NotNull(message = "Points amount is required")
    @Min(value = 1, message = "Points amount must be at least 1")
    private Integer pointsAmount;

    private String description;

    private UUID orderId;
    
    private UUID itemId;
    
    private UUID collectionRequestId;
}









