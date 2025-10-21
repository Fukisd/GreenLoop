package org.greenloop.circularfashion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "users", 
       uniqueConstraints = {
           @UniqueConstraint(columnNames = "email"),
           @UniqueConstraint(columnNames = "username")
       })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;


    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @JsonIgnore
    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    @Column(name = "username", unique = true, length = 50)
    private String username;

    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", length = 50)
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", length = 50)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must be 10 digits and start with 0")
    @Column(name = "phone", length = 20)
    private String phone;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(MALE|FEMALE|OTHER|PREFER_NOT_TO_SAY)$", message = "Gender must be valid")
    @Column(name = "gender", length = 20)
    private String gender;

    // Role and permissions
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    @Builder.Default
    private UserType userType = UserType.CONSUMER;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    // Profile information
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Size(max = 1000, message = "Bio must not exceed 1000 characters")
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    // Points and scoring
    @Min(value = 0, message = "Sustainability points cannot be negative")
    @Column(name = "sustainability_points")
    @Builder.Default
    private Integer sustainabilityPoints = 0;

    @DecimalMin(value = "0.0", message = "Sustainability score cannot be negative")
    @DecimalMax(value = "10.0", message = "Sustainability score cannot exceed 10.0")
    @Column(name = "sustainability_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal sustainabilityScore = BigDecimal.valueOf(0.0);

    @DecimalMin(value = "0.0", message = "Trust score cannot be negative")
    @DecimalMax(value = "10.0", message = "Trust score cannot exceed 10.0")
    @Column(name = "trust_score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal trustScore = BigDecimal.valueOf(5.0);

    // Account status
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "phone_verified")
    @Builder.Default
    private Boolean phoneVerified = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_banned")
    @Builder.Default
    private Boolean isBanned = false;

    // Authentication
    @Size(max = 100, message = "Google ID must not exceed 100 characters")
    @Column(name = "google_id", length = 100)
    private String googleId;

    @Size(max = 100, message = "Firebase UID must not exceed 100 characters")
    @Column(name = "firebase_uid", length = 100)
    private String firebaseUid;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserAddress> addresses = new HashSet<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserFollow> following = new HashSet<>();

    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserFollow> followers = new HashSet<>();

    @OneToMany(mappedBy = "currentOwner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Item> ownedItems = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<CollectionRequest> collectionRequests = new HashSet<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<MarketplaceListing> listings = new HashSet<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> purchases = new HashSet<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Order> sales = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<PointTransaction> pointTransactions = new HashSet<>();

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviewsGiven = new HashSet<>();

    @OneToMany(mappedBy = "reviewedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Review> reviewsReceived = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Post> posts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    // Enums
    public enum UserType {
        CONSUMER, COLLECTOR, BRAND, ADMIN, MODERATOR
    }

    public enum Role {
        USER, ADMIN, STAFF
    }

    // UserDetails implementation
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        authorities.add(new SimpleGrantedAuthority("USER_TYPE_" + userType.name()));
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return passwordHash;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email; // Use email as username
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return isActive;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !isBanned;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return isActive && !isBanned;
    }

    // Helper methods
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return username != null ? username : email;
    }

    public void addSustainabilityPoints(Integer points) {
        if (points != null && points > 0) {
            this.sustainabilityPoints = (this.sustainabilityPoints != null ? this.sustainabilityPoints : 0) + points;
        }
    }

    public void updateTrustScore(BigDecimal newScore) {
        if (newScore != null && newScore.compareTo(BigDecimal.ZERO) >= 0 && newScore.compareTo(BigDecimal.TEN) <= 0) {
            this.trustScore = newScore;
        }
    }

    public boolean canPerformAction(String action) {
        if (isBanned) return false;
        if (!isActive) return false;
        
        return switch (action) {
            case "CREATE_LISTING" -> userType == UserType.CONSUMER || userType == UserType.BRAND;
            case "COLLECT_ITEMS" -> userType == UserType.COLLECTOR || role == Role.ADMIN;
            case "MODERATE_CONTENT" -> userType == UserType.MODERATOR || role == Role.ADMIN;
            case "ADMIN_ACCESS" -> role == Role.ADMIN || role == Role.STAFF;
            default -> true;
        };
    }

    @PrePersist
    protected void onCreate() {
        if (userId == null) {
            userId = UUID.randomUUID();
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