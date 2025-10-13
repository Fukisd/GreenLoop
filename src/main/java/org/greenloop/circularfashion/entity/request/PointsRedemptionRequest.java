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
public class PointsRedemptionRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Points to redeem is required")
    @Min(value = 1, message = "Points to redeem must be at least 1")
    private Integer pointsToRedeem;

    @NotNull(message = "Redemption type is required")
    private String redemptionType; // DISCOUNT, VOUCHER, DONATION, etc.

    private String description;

    private UUID orderId; // If redeeming for order discount
}









