package org.greenloop.circularfashion.entity.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointSummaryResponse {

    private Integer totalEarnedPoints;
    private Integer totalSpentPoints;
    private Integer availablePoints;
    private Integer expiringPoints;
    private Integer expiringInDays;
    private Map<String, Integer> pointsByType;
    private String membershipLevel;
}









