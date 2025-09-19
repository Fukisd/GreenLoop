package org.greenloop.circularfashion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sustainability_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SustainabilityMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metric_id")
    private Long metricId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false)
    private MetricType metricType;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal value;
    
    @Column(length = 20)
    private String unit;
    
    @Column(name = "date_recorded")
    private LocalDate dateRecorded;
    
    @Column(name = "source_activity", length = 100)
    private String sourceActivity;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public enum MetricType {
        CARBON_SAVED,
        WATER_SAVED,
        WASTE_DIVERTED,
        ITEMS_RECYCLED,
        ITEMS_REUSED
    }
} 