package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointTransactionResponse {

    private UUID transactionId;
    private UUID userId;
    private String userName;
    private String transactionType;
    private Integer pointsAmount;
    private String description;
    private Integer balanceBefore;
    private Integer balanceAfter;
    private LocalDateTime expiresAt;
    private String status;
    private LocalDateTime createdAt;
    private UUID orderId;
    private UUID itemId;
    private UUID collectionRequestId;
}









