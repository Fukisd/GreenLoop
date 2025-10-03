package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class ChatRoom {

    @Id
    @GeneratedValue
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "room_name")
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type")
    @Builder.Default
    private RoomType roomType = RoomType.DIRECT;

    // For direct chats between two users
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id")
    @JsonIgnore
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id")
    @JsonIgnore
    private User user2;

    // Related to item discussion
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonIgnore
    private Item item;

    // Related to marketplace listing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    @JsonIgnore
    private MarketplaceListing listing;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_blocked")
    @Builder.Default
    private Boolean isBlocked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by")
    @JsonIgnore
    private User blockedBy;

    @Column(name = "last_activity")
    private LocalDateTime lastActivity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    // Enums
    public enum RoomType {
        DIRECT, GROUP, SUPPORT, LIVE_STREAM
    }

    // Helper methods
    public boolean isDirectChat() {
        return roomType == RoomType.DIRECT;
    }

    public boolean isGroupChat() {
        return roomType == RoomType.GROUP;
    }

    public boolean canUserAccess(User user) {
        if (isBlocked) return false;
        
        return switch (roomType) {
            case DIRECT -> user.equals(user1) || user.equals(user2);
            case GROUP, SUPPORT, LIVE_STREAM -> true; // Additional logic needed for group permissions
        };
    }

    public User getOtherUser(User currentUser) {
        if (!isDirectChat()) return null;
        
        if (currentUser.equals(user1)) {
            return user2;
        } else if (currentUser.equals(user2)) {
            return user1;
        }
        return null;
    }

    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (roomId == null) {
            roomId = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (lastActivity == null) {
            lastActivity = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 