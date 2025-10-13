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
@Table(name = "live_streams", indexes = {
    @Index(name = "idx_livestream_streamer", columnList = "streamer_id"),
    @Index(name = "idx_livestream_status", columnList = "status"),
    @Index(name = "idx_livestream_start_time", columnList = "start_time"),
    @Index(name = "idx_livestream_viewer_count", columnList = "current_viewer_count"),
    @Index(name = "idx_livestream_featured", columnList = "is_featured")
})
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LiveStream extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stream_id")
    private Long streamId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "streamer_id", nullable = false)
    private User streamer;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 50, nullable = false)
    @lombok.Builder.Default
    private String status = "SCHEDULED"; // SCHEDULED, LIVE, ENDED, CANCELLED
    
    @Column(name = "stream_key", unique = true, length = 100)
    private String streamKey;
    
    @Column(name = "stream_url", length = 500)
    private String streamUrl;
    
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;
    
    // Timing
    @Column(name = "scheduled_start_time")
    private LocalDateTime scheduledStartTime;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    // Viewer metrics
    @Column(name = "current_viewer_count")
    @lombok.Builder.Default
    private Long currentViewerCount = 0L;
    
    @Column(name = "peak_viewer_count")
    @lombok.Builder.Default
    private Long peakViewerCount = 0L;
    
    @Column(name = "total_views")
    @lombok.Builder.Default
    private Long totalViews = 0L;
    
    // Engagement metrics
    @Column(name = "like_count")
    @lombok.Builder.Default
    private Long likeCount = 0L;
    
    @Column(name = "comment_count")
    @lombok.Builder.Default
    private Long commentCount = 0L;
    
    @Column(name = "share_count")
    @lombok.Builder.Default
    private Long shareCount = 0L;
    
    // Featured items for sale during stream
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "stream_featured_items",
        joinColumns = @JoinColumn(name = "stream_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> featuredItems;
    
    // Stream categories/tags
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;
    
    // Stream settings
    @Column(name = "is_private", nullable = false)
    @lombok.Builder.Default
    private Boolean isPrivate = false;
    
    @Column(name = "is_featured", nullable = false)
    @lombok.Builder.Default
    private Boolean isFeatured = false;
    
    @Column(name = "allow_comments", nullable = false)
    @lombok.Builder.Default
    private Boolean allowComments = true;
    
    @Column(name = "recording_enabled", nullable = false)
    @lombok.Builder.Default
    private Boolean recordingEnabled = false;
    
    @Column(name = "recording_url", length = 500)
    private String recordingUrl;
    
    // Age restriction and content rating
    @Column(name = "is_age_restricted", nullable = false)
    @lombok.Builder.Default
    private Boolean isAgeRestricted = false;
    
    @Column(name = "content_rating", length = 20)
    @lombok.Builder.Default
    private String contentRating = "GENERAL";
    
    // Monetization
    @Column(name = "total_sales", precision = 10, scale = 2)
    @lombok.Builder.Default
    private java.math.BigDecimal totalSales = java.math.BigDecimal.ZERO;
    
    @Column(name = "commission_earned", precision = 10, scale = 2)
    @lombok.Builder.Default
    private java.math.BigDecimal commissionEarned = java.math.BigDecimal.ZERO;
    
    // Stream metadata
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "stream_metadata", columnDefinition = "jsonb")
    private Map<String, Object> streamMetadata;
    
    // Moderation
    @Column(name = "is_reported", nullable = false)
    @lombok.Builder.Default
    private Boolean isReported = false;
    
    @Column(name = "is_blocked", nullable = false)
    @lombok.Builder.Default
    private Boolean isBlocked = false;
    
    @Column(name = "blocked_reason", length = 500)
    private String blockedReason;
} 


