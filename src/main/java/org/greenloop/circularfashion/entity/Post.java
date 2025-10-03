package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class Post {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private UUID postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Type(JsonType.class)
    @Column(name = "images", columnDefinition = "jsonb")
    private List<String> images;

    @Type(JsonType.class)
    @Column(name = "videos", columnDefinition = "jsonb")
    private List<String> videos;

    // Related to specific item (if post is about an item)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    // Related to marketplace listing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    @JsonIgnore
    private MarketplaceListing listing;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_type")
    @Builder.Default
    private PostType postType = PostType.GENERAL;

    // Engagement metrics
    @Column(name = "likes_count")
    @Builder.Default
    private Integer likesCount = 0;

    @Column(name = "comments_count")
    @Builder.Default
    private Integer commentsCount = 0;

    @Column(name = "shares_count")
    @Builder.Default
    private Integer sharesCount = 0;

    @Column(name = "views_count")
    @Builder.Default
    private Integer viewsCount = 0;

    // Hashtags and tags
    @Type(JsonType.class)
    @Column(name = "hashtags", columnDefinition = "jsonb")
    private List<String> hashtags;

    // Privacy and visibility
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility")
    @Builder.Default
    private Visibility visibility = Visibility.PUBLIC;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Comment> comments = new HashSet<>();

    // Enums
    public enum PostType {
        GENERAL, OUTFIT, STYLING_TIP, SUSTAINABILITY_TIP, ITEM_SHOWCASE, 
        BEFORE_AFTER, RECYCLING_STORY, BRAND_REVIEW, LIVE_STREAM_ANNOUNCEMENT
    }

    public enum Visibility {
        PUBLIC, FOLLOWERS_ONLY, PRIVATE
    }

    // Helper methods
    public boolean hasMedia() {
        return (images != null && !images.isEmpty()) || (videos != null && !videos.isEmpty());
    }

    public boolean isVisible() {
        return !isHidden;
    }

    public boolean isAboutItem() {
        return item != null;
    }

    public boolean isAboutListing() {
        return listing != null;
    }

    public void incrementViews() {
        this.viewsCount = (this.viewsCount != null ? this.viewsCount : 0) + 1;
    }

    public void incrementLikes() {
        this.likesCount = (this.likesCount != null ? this.likesCount : 0) + 1;
    }

    public void decrementLikes() {
        this.likesCount = Math.max(0, (this.likesCount != null ? this.likesCount : 1) - 1);
    }

    public void incrementComments() {
        this.commentsCount = (this.commentsCount != null ? this.commentsCount : 0) + 1;
    }

    public void decrementComments() {
        this.commentsCount = Math.max(0, (this.commentsCount != null ? this.commentsCount : 1) - 1);
    }

    public void incrementShares() {
        this.sharesCount = (this.sharesCount != null ? this.sharesCount : 0) + 1;
    }

    @PrePersist
    protected void onCreate() {
        if (postId == null) {
            postId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 
 