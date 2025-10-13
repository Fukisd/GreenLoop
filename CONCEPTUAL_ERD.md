# GreenLoop - Conceptual Entity Relationship Diagram (ERD)

## Overview
This document describes the conceptual data model for the GreenLoop Circular Fashion platform. The system supports a sustainable fashion marketplace with item tracking, social features, point system, and collection services.

---

## Core Entities

### 1. **User** (Central Entity)
**Purpose**: Manages all user accounts in the system
- **Primary Key**: `user_id` (UUID)
- **Unique Constraints**: `email`, `username`
- **User Types**: CONSUMER, COLLECTOR, BRAND, ADMIN, MODERATOR
- **Roles**: USER, ADMIN, STAFF

**Key Attributes**:
- Authentication: email, password_hash, username
- Profile: first_name, last_name, phone, date_of_birth, gender, avatar_url, bio
- Points & Scores: sustainability_points, sustainability_score, trust_score
- Status: is_verified, email_verified, phone_verified, is_active, is_banned
- OAuth: google_id, firebase_uid

**Relationships**:
- Has many: Items (as owner), Addresses, Listings, Orders (as buyer/seller), Posts, Reviews, CollectionRequests
- Participates in: UserFollow (as follower/followed)

---

### 2. **Item**
**Purpose**: Tracks individual fashion items throughout their lifecycle
- **Primary Key**: `item_id` (UUID)
- **Unique Constraint**: `item_code` (QR/Barcode)

**Key Attributes**:
- Basic Info: name, description, item_code
- Physical: size, color, material_composition (JSON), weight_grams, dimensions (JSON)
- Condition: condition_score (1.0-5.0), condition_description
- Valuation: original_price, current_estimated_value
- Status: item_status (SUBMITTED → COLLECTED → VALUED → LISTED → SOLD/RECYCLED)
- Sustainability: carbon_footprint_kg, water_saved_liters, energy_saved_kwh
- Media: images (JSON array), videos (JSON array)
- Metadata: tags (JSON), metadata (JSON)

**Relationships**:
- Belongs to: Category (required), Brand (optional)
- Owned by: User (original_owner, current_owner)
- Verified by: User
- Has many: ItemLifecycle records, MarketplaceListings, Reviews

---

### 3. **Brand**
**Purpose**: Represents fashion brands in the system
- **Primary Key**: `brand_id` (UUID)
- **Unique Constraint**: `slug`

**Key Attributes**:
- Identity: name, slug, description, logo_url, website
- Sustainability: sustainability_rating, eco_certification (JSON)
- Status: is_verified, is_partner, is_active

**Relationships**:
- Has many: Items

---

### 4. **Category**
**Purpose**: Hierarchical categorization of items
- **Primary Key**: `category_id` (UUID)
- **Unique Constraint**: `slug`
- **Hierarchy**: Self-referencing (parent-child relationship)

**Key Attributes**:
- Identity: name, slug, description, image_url, icon
- Settings: requires_authentication, min_condition_score, display_order
- Status: is_active

**Relationships**:
- Self-referencing: parent_category → sub_categories
- Has many: Items

---

### 5. **MarketplaceListing**
**Purpose**: Items listed for sale, rent, or trade
- **Primary Key**: `listing_id` (UUID)

**Key Attributes**:
- Content: title, description
- Type: listing_type (SELL, RENT, TRADE, FREE)
- Pricing: price, rental_price_per_day/week/month, original_price
- Trading: accepts_trades, preferred_trade_items
- Availability: available_from, available_until, quantity_available
- Rental: min_rental_days, max_rental_days
- Delivery: delivery_available, delivery_fee, delivery_radius_km
- Status: status (DRAFT, ACTIVE, PAUSED, SOLD, RENTED, EXPIRED, REMOVED)
- Metrics: view_count, favorite_count, inquiry_count
- SEO: tags (JSON), keywords

**Relationships**:
- Belongs to: Item (required), User (seller)
- Has: UserAddress (pickup_location)
- Has many: Orders

---

### 6. **Order**
**Purpose**: Tracks purchase, rental, and trade transactions
- **Primary Key**: `order_id` (UUID)
- **Unique Constraint**: `order_number`

**Key Attributes**:
- Identity: order_number (GL-YYYYMMDD-XXXX)
- Type: order_type (PURCHASE, RENTAL, TRADE)
- Pricing: item_price, delivery_fee, service_fee, tax_amount, total_amount
- Rental: rental_start_date, rental_end_date, rental_days, security_deposit
- Payment: payment_method, payment_status, payment_id
- Delivery: delivery_method, tracking_number
- Status: order_status (PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED)
- Notes: buyer_notes, seller_notes, admin_notes
- Timestamps: confirmed_at, shipped_at, delivered_at, completed_at, cancelled_at

**Relationships**:
- Belongs to: User (buyer, seller), MarketplaceListing, Item
- Has: UserAddress (delivery_address)
- Referenced by: Reviews, PointTransactions

---

### 7. **UserAddress**
**Purpose**: User addresses for delivery and collection
- **Primary Key**: `address_id` (UUID)

**Key Attributes**:
- Type: address_type (HOME, WORK, COLLECTION_POINT, OTHER)
- Location: street_address, ward, district, city, province, postal_code, country
- Coordinates: latitude, longitude
- Flags: is_default, is_collection_point
- Label: Custom label

**Relationships**:
- Belongs to: User
- Referenced by: MarketplaceListing (pickup), Order (delivery), CollectionRequest

---

## Social & Engagement Entities

### 8. **UserFollow**
**Purpose**: Social following relationships
- **Primary Key**: `follow_id` (UUID)
- **Unique Constraint**: (follower_id, followed_id)

**Relationships**:
- Links: User (follower) → User (followed)

---

### 9. **Post**
**Purpose**: Social media posts
- **Primary Key**: `post_id` (UUID)

**Key Attributes**:
- Content: content, images (JSON), videos (JSON), hashtags (JSON)
- Type: post_type (GENERAL, OUTFIT, STYLING_TIP, SUSTAINABILITY_TIP, etc.)
- Metrics: likes_count, comments_count, shares_count, views_count
- Visibility: visibility (PUBLIC, FOLLOWERS_ONLY, PRIVATE)
- Flags: is_featured, is_hidden

**Relationships**:
- Belongs to: User
- Can reference: Item, MarketplaceListing
- Has many: Like, Comment

---

### 10. **Like**
**Purpose**: Likes on posts
- **Primary Key**: `like_id` (Long)

**Relationships**:
- Belongs to: Post, User

---

### 11. **Comment**
**Purpose**: Comments on posts
- **Primary Key**: `comment_id` (Long)

**Key Attributes**:
- content, is_edited

**Relationships**:
- Belongs to: Post, User

---

### 12. **Message**
**Purpose**: User-to-user messaging
- **Primary Key**: `message_id` (Long)

**Key Attributes**:
- content, message_type (TEXT, IMAGE, FILE, SYSTEM)
- is_read, is_edited

**Relationships**:
- Belongs to: Conversation OR ChatRoom
- Sender: User

---

### 13. **Review**
**Purpose**: Reviews for users, items, and orders
- **Primary Key**: `review_id` (UUID)

**Key Attributes**:
- Content: rating (1-5), title, content
- Detailed Ratings: quality_rating, communication_rating, delivery_rating, value_rating
- Media: images (JSON), videos (JSON)
- Status: is_verified, is_featured, is_hidden
- Engagement: helpful_count, not_helpful_count

**Relationships**:
- Reviewer: User
- Target (one of): User, Item, Order

---

## Points & Gamification

### 14. **PointTransaction**
**Purpose**: Tracks all point earning and spending activities
- **Primary Key**: `transaction_id` (UUID)

**Key Attributes**:
- Transaction: transaction_type, points_amount, description
- Balance: balance_before, balance_after
- Status: status (PENDING, COMPLETED, CANCELLED, EXPIRED)
- Expiration: expires_at

**Transaction Types**:
- Earning: EARNED_COLLECTION, EARNED_PURCHASE, EARNED_REVIEW, EARNED_REFERRAL
- Spending: SPENT_DISCOUNT, SPENT_PREMIUM
- Other: EXPIRED, ADJUSTMENT

**Relationships**:
- Belongs to: User
- Can reference: Order, Item, CollectionRequest

---

### 15. **PointEarningRule**
**Purpose**: Configurable rules for earning points
- **Primary Key**: `rule_id` (UUID)

**Key Attributes**:
- rule_name, description
- Configuration: points_per_purchase, points_per_collection, points_per_review, points_per_referral
- Bonuses: signup_bonus, daily_login_points
- Status: is_active

---

## Collection & Logistics

### 16. **CollectionRequest**
**Purpose**: Requests for collecting items from users
- **Primary Key**: `request_id` (UUID)

**Key Attributes**:
- Schedule: preferred_date, preferred_time_slot
- Items: estimated_items_count, item_categories (JSON), special_instructions
- Status: status (PENDING, SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED)
- Priority: priority_level (1-5)
- Completion: collected_at, collector_notes

**Relationships**:
- Belongs to: User
- Has: UserAddress (pickup_address)
- Assigned to: User (collector)
- At: CollectionPoint
- Has many: CollectionRequestItem

---

## Item Tracking

### 17. **ItemLifecycle**
**Purpose**: Tracks complete history of item status changes and ownership
- **Primary Key**: `lifecycle_id` (UUID)

**Key Attributes**:
- Status: previous_status, new_status, change_reason, notes
- Ownership: previous_owner, new_owner
- Impact: carbon_impact_kg, energy_impact_kwh
- Audit: changed_by

**Relationships**:
- Belongs to: Item
- References: User (previous_owner, new_owner, changed_by)

---

## Entity Relationship Summary

### Main Relationships Diagram (Text Format)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              USER (Central Entity)                           │
│  - user_id, email, username, role, user_type                                │
│  - sustainability_points, trust_score, sustainability_score                 │
└──────────────┬──────────────────────────────────────────────────────────────┘
               │
   ┌───────────┴─────────────┬────────────┬───────────┬──────────────┬────────┐
   │                         │            │           │              │        │
   ▼                         ▼            ▼           ▼              ▼        ▼
┌──────────┐          ┌──────────┐  ┌─────────┐ ┌──────────┐  ┌──────────┐ ┌──────┐
│  ITEM    │          │ ADDRESS  │  │  POST   │ │  ORDER   │  │ REVIEW   │ │FOLLOW│
│(owner)   │          │          │  │         │ │(buyer/   │  │(reviewer)│ │      │
└────┬─────┘          └────┬─────┘  └────┬────┘ │seller)   │  └──────────┘ └──────┘
     │                     │             │      └─────┬────┘
     │                     │             │            │
     ▼                     ▼             ▼            ▼
┌──────────┐          ┌──────────┐  ┌────────┐  ┌──────────────┐
│ CATEGORY │          │COLLECTION│  │ LIKE   │  │POINT         │
│(hierarchy)│         │ REQUEST  │  │COMMENT │  │TRANSACTION   │
└──────────┘          └──────────┘  └────────┘  └──────────────┘
     │
     ▼
┌──────────┐
│  BRAND   │
└──────────┘

ITEM Relationships:
┌──────────┐       ┌──────────────────┐       ┌─────────────┐
│  ITEM    │──────▶│ MARKETPLACE      │──────▶│   ORDER     │
│          │       │ LISTING          │       │             │
└────┬─────┘       └──────────────────┘       └─────────────┘
     │
     │
     ▼
┌──────────┐
│  ITEM    │
│LIFECYCLE │
│(history) │
└──────────┘
```

---

## Detailed Relationship Matrix

| Entity               | Related To              | Relationship Type | Cardinality |
|---------------------|-------------------------|-------------------|-------------|
| **User**            | Item                    | Owns              | 1:N         |
| **User**            | UserAddress             | Has               | 1:N         |
| **User**            | UserFollow              | Follows/Followed  | N:M         |
| **User**            | MarketplaceListing      | Sells             | 1:N         |
| **User**            | Order                   | Buys/Sells        | 1:N (each)  |
| **User**            | Post                    | Creates           | 1:N         |
| **User**            | Like                    | Likes             | 1:N         |
| **User**            | Comment                 | Comments          | 1:N         |
| **User**            | Review                  | Gives/Receives    | 1:N (each)  |
| **User**            | PointTransaction        | Has               | 1:N         |
| **User**            | CollectionRequest       | Requests          | 1:N         |
| **User**            | Message                 | Sends             | 1:N         |
| **Item**            | Category                | Belongs to        | N:1         |
| **Item**            | Brand                   | Belongs to        | N:1         |
| **Item**            | User                    | Owned by          | N:1 (2x)    |
| **Item**            | ItemLifecycle           | Has               | 1:N         |
| **Item**            | MarketplaceListing      | Listed as         | 1:N         |
| **Item**            | Review                  | Reviewed          | 1:N         |
| **Category**        | Category                | Parent/Child      | 1:N (self)  |
| **Category**        | Item                    | Contains          | 1:N         |
| **Brand**           | Item                    | Manufactures      | 1:N         |
| **MarketplaceListing** | Item                 | Lists             | N:1         |
| **MarketplaceListing** | User                 | Sold by           | N:1         |
| **MarketplaceListing** | UserAddress          | Pickup at         | N:1         |
| **MarketplaceListing** | Order                | Generates         | 1:N         |
| **MarketplaceListing** | Post                 | Featured in       | 1:N         |
| **Order**           | User                    | Buyer/Seller      | N:1 (each)  |
| **Order**           | Item                    | Contains          | N:1         |
| **Order**           | MarketplaceListing      | From              | N:1         |
| **Order**           | UserAddress             | Deliver to        | N:1         |
| **Order**           | Review                  | Can be reviewed   | 1:N         |
| **Order**           | PointTransaction        | Earns points      | 1:N         |
| **Post**            | User                    | Posted by         | N:1         |
| **Post**            | Item                    | About (optional)  | N:1         |
| **Post**            | MarketplaceListing      | About (optional)  | N:1         |
| **Post**            | Like                    | Receives          | 1:N         |
| **Post**            | Comment                 | Receives          | 1:N         |
| **Like**            | Post                    | Likes             | N:1         |
| **Like**            | User                    | By                | N:1         |
| **Comment**         | Post                    | On                | N:1         |
| **Comment**         | User                    | By                | N:1         |
| **Review**          | User                    | Reviewer          | N:1         |
| **Review**          | User                    | Reviewed user     | N:1         |
| **Review**          | Item                    | Reviewed item     | N:1         |
| **Review**          | Order                   | From order        | N:1         |
| **PointTransaction** | User                   | Belongs to        | N:1         |
| **PointTransaction** | Order                  | From (optional)   | N:1         |
| **PointTransaction** | Item                   | From (optional)   | N:1         |
| **PointTransaction** | CollectionRequest      | From (optional)   | N:1         |
| **CollectionRequest** | User                  | Requested by      | N:1         |
| **CollectionRequest** | User                  | Assigned to       | N:1         |
| **CollectionRequest** | UserAddress           | Pickup at         | N:1         |
| **CollectionRequest** | CollectionPoint       | At                | N:1         |
| **ItemLifecycle**   | Item                    | Tracks            | N:1         |
| **ItemLifecycle**   | User                    | Previous/New Owner| N:1 (each)  |
| **ItemLifecycle**   | User                    | Changed by        | N:1         |

---

## Key Business Rules

### Item Lifecycle Flow
```
SUBMITTED → PENDING_COLLECTION → COLLECTED → VALUING → VALUED 
          → PROCESSING → READY_FOR_SALE → LISTED → SOLD/RENTED/DONATED/RECYCLED
```

### Order Lifecycle Flow
```
PENDING → CONFIRMED → PROCESSING → SHIPPED → DELIVERED → COMPLETED
```

### Point System Rules
1. Users earn points for:
   - Making purchases
   - Collecting items for recycling
   - Writing reviews
   - Referring new users
   - Daily login
   - Account signup

2. Points can be spent on:
   - Discounts on purchases
   - Premium features

3. Points may expire based on configuration

### User Types & Permissions
- **CONSUMER**: Can buy, sell, and trade items
- **COLLECTOR**: Can collect items from users
- **BRAND**: Can list brand items
- **ADMIN**: Full system access
- **MODERATOR**: Can moderate content

### Marketplace Listing Types
- **SELL**: One-time purchase
- **RENT**: Temporary use with return
- **TRADE**: Exchange items
- **FREE**: Give away items

---

## Data Integrity Constraints

### Unique Constraints
1. User: email, username
2. Item: item_code
3. Category: slug
4. Brand: slug
5. Order: order_number
6. UserFollow: (follower_id, followed_id)

### Referential Integrity
- All foreign key relationships enforce referential integrity
- Cascade deletes configured for dependent entities
- Orphan prevention on critical relationships

### Business Constraints
1. Item condition_score: 1.0 - 5.0
2. Review rating: 1 - 5
3. User sustainability_score: 0.0 - 10.0
4. User trust_score: 0.0 - 10.0
5. Point transaction amounts can be positive (earned) or negative (spent)
6. Cannot follow yourself (UserFollow validation)

---

## JSON Field Structures

### Item.material_composition
```json
{
  "cotton": 70,
  "polyester": 25,
  "elastane": 5
}
```

### Item.dimensions
```json
{
  "length": 85.5,
  "width": 45.0,
  "height": 2.0
}
```

### Item.images / Item.videos
```json
[
  "https://cloudinary.com/image1.jpg",
  "https://cloudinary.com/image2.jpg"
]
```

### Item.tags
```json
[
  "sustainable",
  "vintage",
  "summer"
]
```

### Brand.eco_certification
```json
{
  "gots": true,
  "fair_trade": true,
  "b_corp": false
}
```

---

## Indexes & Performance Considerations

### Recommended Indexes
1. User: email, username, user_type, role
2. Item: item_code, category_id, brand_id, item_status, current_owner_id
3. MarketplaceListing: seller_id, status, listing_type
4. Order: buyer_id, seller_id, order_status, order_number
5. Review: reviewer_id, reviewed_user_id, item_id, order_id
6. PointTransaction: user_id, transaction_type, status
7. Post: user_id, post_type, visibility
8. CollectionRequest: user_id, status, assigned_collector_id

### JSON Field Indexes (PostgreSQL GIN)
- Item: material_composition, tags
- Post: hashtags
- MarketplaceListing: tags

---

## Database Technology

- **RDBMS**: PostgreSQL
- **ORM**: Hibernate/JPA
- **JSON Support**: JSONB columns for flexible data
- **UUID**: Primary keys for distributed systems
- **Timestamps**: Automatic creation and update tracking
- **Soft Deletes**: Status flags instead of hard deletes

---

## Version Information

- **Document Version**: 1.0
- **Last Updated**: October 13, 2025
- **Backend Framework**: Spring Boot
- **Database**: PostgreSQL with JSONB support
- **Schema Management**: Hibernate Auto-DDL / Flyway migrations

---

## Notes

1. All entities use UUID as primary keys except Like, Comment, and Message (use Long)
2. All entities have created_at and updated_at timestamps (except where noted)
3. Lazy loading is used for all relationships to optimize performance
4. JSON fields provide flexibility for evolving requirements
5. The system supports multi-tenancy through user types
6. Sustainability metrics are tracked at multiple levels (User, Item, Brand)
7. The platform emphasizes circular economy principles through item lifecycle tracking

