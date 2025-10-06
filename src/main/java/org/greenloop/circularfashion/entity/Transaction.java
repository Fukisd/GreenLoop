package org.greenloop.circularfashion.entity;

import org.greenloop.circularfashion.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_transaction_buyer", columnList = "buyer_id"),
    @Index(name = "idx_transaction_seller", columnList = "seller_id"),
    @Index(name = "idx_transaction_item", columnList = "item_id"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    @Index(name = "idx_transaction_status", columnList = "status"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date"),
    @Index(name = "idx_transaction_reference", columnList = "payment_reference")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Transaction extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // nullable for collection rewards
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // nullable for point transactions
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(name = "points_used", precision = 10, scale = 2)
    private BigDecimal pointsUsed;
    
    @Column(name = "points_earned", precision = 10, scale = 2)
    private BigDecimal pointsEarned;
    
    @Column(length = 50, nullable = false)
    private String status; // PENDING, COMPLETED, CANCELLED, REFUNDED
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;
    
    @Column(name = "payment_reference", length = 100)
    private String paymentReference;
    
    // Rental specific fields
    @Column(name = "rental_start_date")
    private LocalDateTime rentalStartDate;
    
    @Column(name = "rental_end_date")
    private LocalDateTime rentalEndDate;
    
    @Column(name = "rental_returned_date")
    private LocalDateTime rentalReturnedDate;
    
    @Column(name = "late_fee", precision = 10, scale = 2)
    private BigDecimal lateFee;
    
    // Shipping and delivery
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "shipping_address", columnDefinition = "jsonb")
    private Map<String, String> shippingAddress;
    
    @Column(name = "shipping_method", length = 50)
    private String shippingMethod;
    
    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;
    
    @Column(name = "shipped_date")
    private LocalDateTime shippedDate;
    
    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;
    
    // Additional transaction details
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    // Commission and fees
    @Column(name = "platform_fee", precision = 10, scale = 2)
    private BigDecimal platformFee;
    
    @Column(name = "shipping_fee", precision = 10, scale = 2)
    private BigDecimal shippingFee;
    
    @Column(name = "insurance_fee", precision = 10, scale = 2)
    private BigDecimal insuranceFee;
    
    // Review status
    @Column(name = "buyer_reviewed", nullable = false)
    @lombok.Builder.Default
    private Boolean buyerReviewed = false;
    
    @Column(name = "seller_reviewed", nullable = false)
    @lombok.Builder.Default
    private Boolean sellerReviewed = false;
} 

