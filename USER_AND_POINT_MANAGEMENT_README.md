# Comprehensive User Management and Point Management System

## Overview

This document describes the comprehensive user management and point management system implemented for the Green Loop circular fashion platform. The system provides robust features for managing users, tracking sustainability points, and implementing a rewards program.

## Table of Contents

1. [Architecture](#architecture)
2. [User Management](#user-management)
3. [Point Management](#point-management)
4. [API Endpoints](#api-endpoints)
5. [Database Schema](#database-schema)
6. [Configuration](#configuration)
7. [Usage Examples](#usage-examples)

---

## Architecture

### Components

The system is built with the following components:

#### Entities
- **User**: Main user entity with profile information and sustainability scoring
- **PointTransaction**: Records all point-related transactions
- **PointEarningRule**: Configurable rules for point earning and redemption

#### Repositories
- **UserRepository**: Database access for users
- **PointTransactionRepository**: Database access for point transactions with custom queries
- **PointEarningRuleRepository**: Database access for point earning rules
- **OrderRepository**: Database access for orders
- **CollectionRequestRepository**: Database access for collection requests

#### Services
- **UserService/UserServiceImpl**: Comprehensive user management operations
- **PointService/PointServiceImpl**: Point earning, redemption, and tracking
- **EmailService**: Email notifications for users

#### Controllers
- **UserController**: REST API for user management
- **PointController**: REST API for point operations
- **PointEarningRuleController**: REST API for managing point rules

#### DTOs
- **Request DTOs**: UserUpdateRequest, PointEarningRequest, PointsRedemptionRequest
- **Response DTOs**: UserDetailResponse, PointTransactionResponse, PointSummaryResponse

---

## User Management

### Features

#### 1. User CRUD Operations
- Get user by ID, email, or username
- Update user profile information
- Delete user accounts
- Activate/deactivate users
- Ban/unban users
- Verify user accounts

#### 2. User Search and Filtering
- Search users by keyword
- Filter by user type (CONSUMER, COLLECTOR, BRAND, ADMIN, MODERATOR)
- Filter by role (USER, ADMIN, STAFF)
- Get active/banned users

#### 3. User Statistics
- Total users count
- Active users count
- Banned users count
- Verified users count
- Users grouped by type and role
- Individual user statistics (followers, items, orders, etc.)

#### 4. Profile Management
- Update profile information
- Update avatar
- Manage bio and personal details

#### 5. Email & Phone Verification
- Send verification emails
- Verify email with token
- Verify phone with code

#### 6. Password Management
- Change password
- Reset password via email
- Update password with reset token

#### 7. Social Features
- Follow/unfollow users
- Get followers count
- Get following count
- Check if following another user

#### 8. Trust and Sustainability Scoring
- Automatic trust score calculation
- Sustainability score based on activities
- Score updates triggered by user actions

### User Fields

```java
- userId: UUID
- email: String (unique)
- username: String (unique)
- firstName, lastName: String
- phone: String
- dateOfBirth: LocalDate
- gender: String
- userType: UserType (CONSUMER, COLLECTOR, BRAND, ADMIN, MODERATOR)
- role: Role (USER, ADMIN, STAFF)
- avatarUrl: String
- bio: String
- sustainabilityPoints: Integer
- sustainabilityScore: BigDecimal (0.0 - 10.0)
- trustScore: BigDecimal (0.0 - 10.0)
- isVerified, emailVerified, phoneVerified: Boolean
- isActive, isBanned: Boolean
```

---

## Point Management

### Features

#### 1. Point Transactions
- Earn points through various activities
- Redeem points for rewards and discounts
- Manual point adjustments by admin
- Transaction history tracking

#### 2. Point Earning Activities
- **Purchase Points**: Earn points on every purchase
- **Collection Points**: Earn points for recycling items
- **Review Points**: Earn points for writing reviews
- **Referral Points**: Earn points for referring new users
- **Signup Bonus**: Welcome points for new users
- **Daily Login Points**: Points for daily engagement

#### 3. Point Redemption
- Discount on purchases
- Vouchers and rewards
- Minimum redemption thresholds
- Point value in currency conversion

#### 4. Point Expiration
- Configurable expiration period
- Automatic expiration processing (scheduled daily at 2 AM)
- Expiry notifications
- Track expiring points

#### 5. Point Statistics
- Total earned points
- Total spent points
- Available points
- Expiring points (7 days, 30 days)
- Points breakdown by transaction type

#### 6. Point Validation
- Check if user has enough points
- Validate redemption eligibility
- Minimum redemption amount checks

### Point Transaction Types

#### Earned Points
- `EARNED_COLLECTION`: Points from recycling collection
- `EARNED_PURCHASE`: Points from purchases
- `EARNED_REVIEW`: Points from writing reviews
- `EARNED_REFERRAL`: Points from referring users

#### Spent Points
- `SPENT_DISCOUNT`: Points used for discounts
- `SPENT_PREMIUM`: Points used for premium features

#### Other
- `EXPIRED`: Points that have expired
- `ADJUSTMENT`: Manual admin adjustments

### Point Earning Rules

Configurable rules include:

```java
- pointsPerPurchase: Integer (points per $1 spent)
- pointsPerCollection: Integer
- pointsPerReview: Integer
- pointsPerReferral: Integer
- signupBonus: Integer
- dailyLoginPoints: Integer
- pointValueInCurrency: Integer (1 point = X VND)
- minimumRedemptionPoints: Integer
- pointsExpireInDays: Integer
- expirationEnabled: Boolean
- eventMultiplier: Double (for special events)
- eventStartDate, eventEndDate: LocalDateTime
```

---

## API Endpoints

### User Management Endpoints

#### User Retrieval
```http
GET /api/users/{userId}                    # Get user by ID
GET /api/users/email/{email}               # Get user by email
GET /api/users/username/{username}         # Get user by username
GET /api/users                             # Get all users (paginated)
GET /api/users/search?keyword=...          # Search users
GET /api/users/type/{userType}             # Get users by type
GET /api/users/role/{role}                 # Get users by role
GET /api/users/active                      # Get active users
GET /api/users/banned                      # Get banned users
```

#### User Update
```http
PUT /api/users/{userId}                    # Update user
PATCH /api/users/{userId}/avatar           # Update avatar
```

#### User Management
```http
DELETE /api/users/{userId}                 # Delete user
PATCH /api/users/{userId}/activate         # Activate user
PATCH /api/users/{userId}/deactivate       # Deactivate user
PATCH /api/users/{userId}/ban              # Ban user
PATCH /api/users/{userId}/unban            # Unban user
PATCH /api/users/{userId}/verify           # Verify user
```

#### Statistics
```http
GET /api/users/management/summary          # User management summary
GET /api/users/{userId}/statistics         # User statistics
GET /api/users/statistics/total            # Total users count
```

#### Verification
```http
POST /api/users/{userId}/send-verification # Send verification email
POST /api/users/{userId}/verify-email      # Verify email
POST /api/users/{userId}/verify-phone      # Verify phone
```

#### Password
```http
POST /api/users/{userId}/change-password   # Change password
POST /api/users/reset-password             # Request password reset
POST /api/users/reset-password/confirm     # Confirm password reset
```

#### Social
```http
POST /api/users/{followerId}/follow/{followedId}    # Follow user
DELETE /api/users/{followerId}/unfollow/{followedId} # Unfollow user
GET /api/users/{userId}/followers/count             # Followers count
GET /api/users/{userId}/following/count             # Following count
```

#### Scores
```http
PATCH /api/users/{userId}/update-trust-score           # Update trust score
PATCH /api/users/{userId}/update-sustainability-score  # Update sustainability score
```

### Point Management Endpoints

#### Transactions
```http
POST /api/points/earn                      # Earn points
POST /api/points/redeem                    # Redeem points
POST /api/points/adjust                    # Adjust points (admin)
```

#### Queries
```http
GET /api/points/summary/{userId}                        # Point summary
GET /api/points/transactions/{userId}                   # All transactions
GET /api/points/transactions/{userId}/recent            # Recent transactions
GET /api/points/transactions/{userId}/type/{type}       # By type
GET /api/points/transactions/{userId}/date-range        # By date range
```

#### Calculations
```http
GET /api/points/{userId}/available         # Available points
GET /api/points/{userId}/earned            # Total earned
GET /api/points/{userId}/spent             # Total spent
GET /api/points/{userId}/expiring          # Expiring points
```

#### Expiration
```http
POST /api/points/expire                    # Trigger expiration
GET /api/points/{userId}/expiring-soon     # Expiring soon details
POST /api/points/{userId}/notify-expiring  # Send expiry notification
```

#### Statistics
```http
GET /api/points/{userId}/statistics        # Point statistics
GET /api/points/{userId}/points-by-type    # Points by type
```

#### Award Points
```http
POST /api/points/award/purchase            # Award purchase points
POST /api/points/award/collection          # Award collection points
POST /api/points/award/review              # Award review points
POST /api/points/award/referral            # Award referral points
POST /api/points/award/signup              # Award signup bonus
POST /api/points/award/daily-login         # Award daily login
```

#### Validation
```http
GET /api/points/{userId}/has-enough        # Check sufficient points
GET /api/points/{userId}/can-redeem        # Check redemption eligibility
```

### Point Earning Rule Endpoints

```http
GET /api/point-rules                       # Get all rules
GET /api/point-rules/active                # Get active rule
GET /api/point-rules/{ruleId}              # Get rule by ID
POST /api/point-rules                      # Create rule
PUT /api/point-rules/{ruleId}              # Update rule
DELETE /api/point-rules/{ruleId}           # Delete rule
PATCH /api/point-rules/{ruleId}/activate   # Activate rule
```

---

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    username VARCHAR(50) UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    user_type VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    avatar_url VARCHAR(500),
    bio TEXT,
    sustainability_points INTEGER DEFAULT 0,
    sustainability_score DECIMAL(5,2) DEFAULT 0.0,
    trust_score DECIMAL(5,2) DEFAULT 5.0,
    is_verified BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_banned BOOLEAN DEFAULT FALSE,
    google_id VARCHAR(100),
    firebase_uid VARCHAR(100),
    last_login TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Point Transactions Table
```sql
CREATE TABLE point_transactions (
    transaction_id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(user_id),
    transaction_type VARCHAR(50) NOT NULL,
    points_amount INTEGER NOT NULL,
    description TEXT,
    balance_before INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    expires_at TIMESTAMP,
    status VARCHAR(20) DEFAULT 'COMPLETED',
    order_id UUID REFERENCES orders(order_id),
    item_id UUID REFERENCES items(item_id),
    collection_request_id UUID REFERENCES collection_requests(request_id),
    created_at TIMESTAMP
);
```

### Point Earning Rules Table
```sql
CREATE TABLE point_earning_rules (
    rule_id UUID PRIMARY KEY,
    rule_name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    points_per_purchase INTEGER DEFAULT 10,
    points_per_collection INTEGER DEFAULT 50,
    points_per_review INTEGER DEFAULT 20,
    points_per_referral INTEGER DEFAULT 100,
    signup_bonus INTEGER DEFAULT 50,
    daily_login_points INTEGER DEFAULT 5,
    point_value_in_currency INTEGER DEFAULT 100,
    minimum_redemption_points INTEGER DEFAULT 100,
    points_expire_in_days INTEGER DEFAULT 365,
    expiration_enabled BOOLEAN DEFAULT TRUE,
    event_multiplier DOUBLE DEFAULT 1.0,
    event_start_date TIMESTAMP,
    event_end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## Configuration

### Application Properties

Add the following to your `application.properties` or `application.yml`:

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Point System Configuration (optional - can be managed via API)
greenloop.points.default-expiration-days=365
greenloop.points.minimum-redemption=100
greenloop.points.currency-value=100
```

### Scheduled Tasks

The system includes a scheduled task for point expiration:

```java
@Scheduled(cron = "0 0 2 * * *") // Runs daily at 2 AM
public void expirePoints()
```

To enable scheduling, ensure your main application class has:

```java
@SpringBootApplication
@EnableScheduling
public class GreenLoopApplication {
    // ...
}
```

---

## Usage Examples

### 1. Awarding Points for a Purchase

```java
// Automatically award points when order is completed
PointTransactionResponse transaction = pointService.awardPurchasePoints(
    userId,
    orderId,
    purchaseAmount
);
```

### 2. Redeeming Points for Discount

```java
PointsRedemptionRequest request = PointsRedemptionRequest.builder()
    .userId(userId)
    .pointsToRedeem(500)
    .redemptionType("DISCOUNT")
    .description("Discount on order")
    .orderId(orderId)
    .build();

PointTransactionResponse transaction = pointService.redeemPoints(request);
```

### 3. Getting Point Summary

```java
PointSummaryResponse summary = pointService.getPointSummary(userId);
System.out.println("Available Points: " + summary.getAvailablePoints());
System.out.println("Expiring in 30 days: " + summary.getExpiringPoints());
```

### 4. Checking User Statistics

```java
Map<String, Object> stats = userService.getUserStatistics(userId);
System.out.println("Sustainability Points: " + stats.get("sustainabilityPoints"));
System.out.println("Trust Score: " + stats.get("trustScore"));
```

### 5. Creating a Point Earning Rule

```java
PointEarningRule rule = PointEarningRule.builder()
    .ruleName("Summer2024Special")
    .description("Double points during summer")
    .pointsPerPurchase(10)
    .pointsPerCollection(100)
    .eventMultiplier(2.0)
    .eventStartDate(LocalDateTime.of(2024, 6, 1, 0, 0))
    .eventEndDate(LocalDateTime.of(2024, 8, 31, 23, 59))
    .isActive(true)
    .build();

pointEarningRuleRepository.save(rule);
```

### 6. Updating User Profile

```java
UserUpdateRequest request = UserUpdateRequest.builder()
    .firstName("John")
    .lastName("Doe")
    .bio("Passionate about sustainable fashion")
    .build();

UserDetailResponse updated = userService.updateUser(userId, request);
```

---

## Security Considerations

### Authentication & Authorization

The system uses Spring Security with role-based access control:

- **USER**: Can manage their own profile and points
- **STAFF**: Can view user management summaries
- **ADMIN**: Full access to all management features
- **MODERATOR**: Can ban/unban users

### Password Security

- Passwords are hashed using BCrypt
- Password reset tokens expire after use
- Email verification required for sensitive operations

### Data Privacy

- Personal information protected by authentication
- Email addresses not exposed in public APIs
- User data access logged for audit purposes

---

## Best Practices

### 1. Point Expiration Notifications

Send notifications to users 7 days before points expire:

```java
// Schedule this to run weekly
List<User> users = userRepository.findAll();
for (User user : users) {
    Integer expiringPoints = pointService.getExpiringPoints(user.getUserId(), 7);
    if (expiringPoints > 0) {
        pointService.notifyExpiringPoints(user.getUserId());
    }
}
```

### 2. Trust Score Calculation

Update trust scores after significant events:

```java
// After successful transaction
userService.updateTrustScore(userId);

// After completing profile
userService.updateTrustScore(userId);
```

### 3. Sustainability Score Updates

Update sustainability scores based on eco-friendly actions:

```java
// After recycling items
userService.updateSustainabilityScore(userId);

// After sustainable purchases
userService.updateSustainabilityScore(userId);
```

### 4. Point Transaction Logging

All point transactions are automatically logged with:
- Balance before and after
- Transaction type
- Related entities (order, item, collection request)
- Expiration date
- Status

---

## Troubleshooting

### Common Issues

#### 1. Points Not Expiring

**Solution**: Ensure `@EnableScheduling` is added to your main application class and the cron expression is correct.

#### 2. Email Notifications Not Sending

**Solution**: Check SMTP configuration in application.properties and ensure email credentials are correct.

#### 3. Insufficient Points Error

**Solution**: Use the validation endpoints to check if user has enough points before redemption.

#### 4. User Not Found Errors

**Solution**: Ensure UUIDs are passed correctly and users exist in the database.

---

## Future Enhancements

### Planned Features

1. **Point Transfer**: Allow users to transfer points to other users
2. **Point Packages**: Buy points with real money
3. **Tier System**: Bronze, Silver, Gold tiers based on points
4. **Achievement Badges**: Special badges for sustainability milestones
5. **Point History Export**: Download transaction history as CSV/PDF
6. **Mobile Push Notifications**: For expiring points and new rewards
7. **Referral Tracking**: Detailed referral analytics
8. **Leaderboards**: Top users by sustainability points

---

## Support

For issues or questions about the user and point management system:

1. Check the API documentation at `/swagger-ui.html`
2. Review the logs for detailed error messages
3. Consult this README for usage examples
4. Contact the development team

---

## License

This system is part of the Green Loop circular fashion platform.

---

**Last Updated**: 2024
**Version**: 1.0.0
**Maintained By**: Green Loop Development Team









