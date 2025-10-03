package org.greenloop.circularfashion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "reports", indexes = {
    @Index(name = "idx_report_reporter", columnList = "reporter_id"),
    @Index(name = "idx_report_reported_user", columnList = "reported_user_id"),
    @Index(name = "idx_report_item", columnList = "reported_item_id"),
    @Index(name = "idx_report_type", columnList = "report_type"),
    @Index(name = "idx_report_status", columnList = "status"),
    @Index(name = "idx_report_priority", columnList = "priority"),
    @Index(name = "idx_report_created", columnList = "created_at")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Report extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    
    @Column(name = "report_type", length = 50, nullable = false)
    private String reportType; // USER_ABUSE, ITEM_FRAUD, INAPPROPRIATE_CONTENT, SPAM, etc.
    
    // What is being reported (one of these will be set)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id")
    private User reportedUser;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_item_id")
    private Item reportedItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_transaction_id")
    private Transaction reportedTransaction;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_livestream_id")
    private LiveStream reportedLivestream;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_message_id")
    private ChatMessage reportedMessage;
    
    @Column(name = "subject", length = 200, nullable = false)
    private String subject;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;
    
    // Evidence attachments
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "evidence_attachments", columnDefinition = "jsonb")
    private List<Map<String, String>> evidenceAttachments; // [{type, url, description}]
    
    @Column(length = 20, nullable = false)
    @lombok.Builder.Default
    private String status = "PENDING"; // PENDING, UNDER_REVIEW, RESOLVED, DISMISSED, ESCALATED
    
    @Column(length = 20, nullable = false)
    @lombok.Builder.Default
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL
    
    // Admin handling
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedByAdmin;
    
    // Actions taken
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "actions_taken", columnDefinition = "jsonb")
    private List<String> actionsTaken; // ["USER_WARNING", "ITEM_REMOVED", "ACCOUNT_SUSPENDED", etc.]
    
    // Follow-up and escalation
    @Column(name = "is_escalated", nullable = false)
    @lombok.Builder.Default
    private Boolean isEscalated = false;
    
    @Column(name = "escalated_at")
    private LocalDateTime escalatedAt;
    
    @Column(name = "escalation_reason", columnDefinition = "TEXT")
    private String escalationReason;
    
    // Reporter feedback
    @Column(name = "reporter_satisfied", nullable = false)
    @lombok.Builder.Default
    private Boolean reporterSatisfied = null; // null = no feedback yet
    
    @Column(name = "reporter_feedback", columnDefinition = "TEXT")
    private String reporterFeedback;
    
    // System generated reports (from AI/automation)
    @Column(name = "is_system_generated", nullable = false)
    @lombok.Builder.Default
    private Boolean isSystemGenerated = false;
    
    @Column(name = "ai_confidence_score", precision = 5, scale = 4)
    private java.math.BigDecimal aiConfidenceScore; // 0.0000 to 1.0000
    
    // Additional metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
} 