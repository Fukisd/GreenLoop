-- Fashion Recycling Platform Database Schema
-- Version 1.0 - Initial Schema

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================
-- CORE USER MANAGEMENT
-- ================================

-- Users table (enhanced for recycling platform)
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    username VARCHAR(100) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    
    -- Role and permissions
    user_type VARCHAR(50) NOT NULL DEFAULT 'CONSUMER',
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    
    -- Profile information
    avatar_url VARCHAR(500),
    bio TEXT,
    
    -- Points and scoring
    sustainability_points INTEGER DEFAULT 0,
    sustainability_score DECIMAL(5,2) DEFAULT 0.0,
    trust_score DECIMAL(5,2) DEFAULT 5.0,
    
    -- Account status
    is_verified BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_banned BOOLEAN DEFAULT FALSE,
    
    -- Authentication
    google_id VARCHAR(255),
    firebase_uid VARCHAR(255),
    last_login TIMESTAMP,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_user_type CHECK (user_type IN ('CONSUMER', 'COLLECTOR', 'BRAND', 'ADMIN', 'MODERATOR')),
    CONSTRAINT check_role CHECK (role IN ('USER', 'PREMIUM', 'COLLECTOR', 'ADMIN', 'SUPER_ADMIN')),
    CONSTRAINT check_trust_score CHECK (trust_score >= 0 AND trust_score <= 10)
);

-- User addresses (supports multiple addresses)
CREATE TABLE user_addresses (
    address_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    
    address_type VARCHAR(50) NOT NULL,
    label VARCHAR(100),
    
    -- Address details (Vietnam-focused)
    street_address TEXT NOT NULL,
    ward VARCHAR(100),
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Vietnam',
    
    -- Coordinates for collection routing
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    is_default BOOLEAN DEFAULT FALSE,
    is_collection_point BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_address_type CHECK (address_type IN ('HOME', 'WORK', 'COLLECTION_POINT', 'OTHER'))
);

-- User follows (social features)
CREATE TABLE user_follows (
    follow_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    follower_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    followed_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(follower_id, followed_id),
    CONSTRAINT no_self_follow CHECK (follower_id != followed_id)
);

-- ================================
-- PRODUCT CATALOG
-- ================================

-- Categories for fashion items
CREATE TABLE categories (
    category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    parent_category_id UUID REFERENCES categories(category_id),
    description TEXT,
    image_url VARCHAR(500),
    icon VARCHAR(100),
    
    -- Category specific settings
    requires_authentication BOOLEAN DEFAULT FALSE,
    min_condition_score DECIMAL(3,2) DEFAULT 1.0,
    
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Brands
CREATE TABLE brands (
    brand_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    website VARCHAR(255),
    
    -- Sustainability metrics
    sustainability_rating DECIMAL(3,2) DEFAULT 0.0,
    eco_certification JSONB,
    
    -- Status
    is_verified BOOLEAN DEFAULT FALSE,
    is_partner BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_sustainability_rating CHECK (sustainability_rating >= 0 AND sustainability_rating <= 5)
);

-- ================================
-- ITEM MANAGEMENT & LIFECYCLE
-- ================================

-- Core items table (fashion items in the system)
CREATE TABLE items (
    item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_code VARCHAR(50) UNIQUE NOT NULL, -- QR/Barcode for tracking
    
    -- Basic information
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id UUID NOT NULL REFERENCES categories(category_id),
    brand_id UUID REFERENCES brands(brand_id),
    
    -- Physical properties
    size VARCHAR(50),
    color VARCHAR(100),
    material_composition JSONB, -- {"cotton": 70, "polyester": 30}
    weight_grams INTEGER,
    dimensions JSONB, -- {"length": 50, "width": 40, "height": 2}
    
    -- Condition and valuation
    condition_score DECIMAL(3,2) NOT NULL, -- 1.0 to 5.0
    condition_description TEXT,
    original_price DECIMAL(12,2),
    current_estimated_value DECIMAL(12,2),
    
    -- Ownership and lifecycle
    original_owner_id UUID REFERENCES users(user_id),
    current_owner_id UUID REFERENCES users(user_id),
    acquisition_method VARCHAR(50) NOT NULL, -- COLLECTED, PURCHASED, TRADED, etc.
    
    -- Item status in system
    item_status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    is_verified BOOLEAN DEFAULT FALSE,
    verification_date TIMESTAMP,
    verified_by UUID REFERENCES users(user_id),
    
    -- Sustainability data
    carbon_footprint_kg DECIMAL(10,4),
    water_saved_liters DECIMAL(10,2),
    energy_saved_kwh DECIMAL(10,2),
    
    -- Media
    images JSONB, -- Array of image URLs
    videos JSONB, -- Array of video URLs
    
    -- Metadata
    tags JSONB, -- Searchable tags
    metadata JSONB, -- Additional flexible data
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_condition_score CHECK (condition_score >= 1.0 AND condition_score <= 5.0),
    CONSTRAINT check_item_status CHECK (item_status IN (
        'SUBMITTED', 'PENDING_COLLECTION', 'COLLECTED', 'VALUING', 'VALUED', 
        'PROCESSING', 'READY_FOR_SALE', 'LISTED', 'SOLD', 'RENTED', 'DONATED', 
        'RECYCLED', 'REJECTED'
    )),
    CONSTRAINT check_acquisition_method CHECK (acquisition_method IN (
        'COLLECTED', 'PURCHASED', 'TRADED', 'DONATED', 'IMPORTED'
    ))
);

-- Item lifecycle history (tracks all changes)
CREATE TABLE item_lifecycle (
    lifecycle_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    previous_owner_id UUID REFERENCES users(user_id),
    new_owner_id UUID REFERENCES users(user_id),
    
    change_reason VARCHAR(255),
    notes TEXT,
    
    -- Environmental impact of this change
    carbon_impact_kg DECIMAL(10,4),
    energy_impact_kwh DECIMAL(10,2),
    
    changed_by UUID REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- COLLECTION SYSTEM
-- ================================

-- Collection points (physical locations)
CREATE TABLE collection_points (
    point_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    point_type VARCHAR(50) NOT NULL,
    
    -- Location
    address_id UUID NOT NULL REFERENCES user_addresses(address_id),
    
    -- Capacity and status
    max_capacity INTEGER DEFAULT 100,
    current_capacity INTEGER DEFAULT 0,
    
    -- Operating information
    operating_hours JSONB, -- {"monday": {"open": "08:00", "close": "18:00"}}
    contact_info JSONB,
    
    -- IoT integration ready
    iot_device_id VARCHAR(100),
    sensor_data JSONB,
    
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_point_type CHECK (point_type IN ('RESIDENTIAL', 'PUBLIC', 'COMMERCIAL', 'PARTNER'))
);

-- Collection requests from users
CREATE TABLE collection_requests (
    request_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Collection details
    pickup_address_id UUID NOT NULL REFERENCES user_addresses(address_id),
    preferred_date DATE,
    preferred_time_slot VARCHAR(50),
    
    -- Items to be collected
    estimated_items_count INTEGER,
    item_categories JSONB, -- Categories of items to collect
    special_instructions TEXT,
    
    -- Request status
    status VARCHAR(50) DEFAULT 'PENDING',
    priority_level INTEGER DEFAULT 3, -- 1-5, 5 is highest
    
    -- Assignment
    assigned_collector_id UUID REFERENCES users(user_id),
    collection_point_id UUID REFERENCES collection_points(point_id),
    
    -- Completion
    collected_at TIMESTAMP,
    collector_notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT check_priority CHECK (priority_level >= 1 AND priority_level <= 5)
);

-- Collection request items (items submitted for collection)
CREATE TABLE collection_request_items (
    request_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    collection_request_id UUID NOT NULL REFERENCES collection_requests(request_id) ON DELETE CASCADE,
    item_id UUID REFERENCES items(item_id), -- Linked after valuation
    
    -- User-provided information
    user_description TEXT,
    user_estimated_value DECIMAL(10,2),
    user_images JSONB,
    
    -- Valuation results
    valuator_id UUID REFERENCES users(user_id),
    final_valuation DECIMAL(10,2),
    valuation_notes TEXT,
    valuation_date TIMESTAMP,
    
    -- Points awarded
    points_awarded INTEGER DEFAULT 0,
    
    status VARCHAR(50) DEFAULT 'PENDING',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_item_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'PROCESSING'))
);

-- ================================
-- MARKETPLACE
-- ================================

-- Marketplace listings
CREATE TABLE marketplace_listings (
    listing_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(item_id),
    seller_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Listing details
    title VARCHAR(255) NOT NULL,
    description TEXT,
    listing_type VARCHAR(50) NOT NULL,
    
    -- Pricing
    price DECIMAL(12,2),
    rental_price_per_day DECIMAL(10,2),
    rental_price_per_week DECIMAL(10,2),
    rental_price_per_month DECIMAL(10,2),
    original_price DECIMAL(12,2),
    
    -- Trading
    accepts_trades BOOLEAN DEFAULT FALSE,
    preferred_trade_items TEXT,
    
    -- Availability
    available_from DATE DEFAULT CURRENT_DATE,
    available_until DATE,
    quantity_available INTEGER DEFAULT 1,
    min_rental_days INTEGER DEFAULT 1,
    max_rental_days INTEGER DEFAULT 30,
    
    -- Location and delivery
    pickup_location_id UUID REFERENCES user_addresses(address_id),
    delivery_available BOOLEAN DEFAULT FALSE,
    delivery_fee DECIMAL(10,2),
    delivery_radius_km INTEGER,
    
    -- Status and visibility
    status VARCHAR(50) DEFAULT 'DRAFT',
    is_featured BOOLEAN DEFAULT FALSE,
    boost_expires_at TIMESTAMP,
    
    -- Metrics
    view_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0,
    inquiry_count INTEGER DEFAULT 0,
    
    -- SEO and searchability
    tags JSONB,
    keywords VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_listing_type CHECK (listing_type IN ('SELL', 'RENT', 'TRADE', 'FREE')),
    CONSTRAINT check_status CHECK (status IN ('DRAFT', 'ACTIVE', 'PAUSED', 'SOLD', 'RENTED', 'EXPIRED', 'REMOVED'))
);

-- ================================
-- TRANSACTIONS & ORDERS
-- ================================

-- Orders/Transactions
CREATE TABLE orders (
    order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    
    buyer_id UUID NOT NULL REFERENCES users(user_id),
    seller_id UUID NOT NULL REFERENCES users(user_id),
    listing_id UUID NOT NULL REFERENCES marketplace_listings(listing_id),
    item_id UUID NOT NULL REFERENCES items(item_id),
    
    -- Order details
    order_type VARCHAR(50) NOT NULL,
    quantity INTEGER DEFAULT 1,
    
    -- Pricing breakdown
    item_price DECIMAL(12,2),
    delivery_fee DECIMAL(10,2) DEFAULT 0,
    service_fee DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    
    -- Rental specific
    rental_start_date DATE,
    rental_end_date DATE,
    rental_days INTEGER,
    security_deposit DECIMAL(12,2),
    
    -- Payment
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_id VARCHAR(255), -- External payment provider ID
    
    -- Delivery/Pickup
    delivery_method VARCHAR(50),
    delivery_address_id UUID REFERENCES user_addresses(address_id),
    tracking_number VARCHAR(100),
    
    -- Status
    order_status VARCHAR(50) DEFAULT 'PENDING',
    
    -- Special instructions
    buyer_notes TEXT,
    seller_notes TEXT,
    admin_notes TEXT,
    
    -- Important dates
    confirmed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_order_type CHECK (order_type IN ('PURCHASE', 'RENTAL', 'TRADE')),
    CONSTRAINT check_payment_status CHECK (payment_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT check_order_status CHECK (order_status IN (
        'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 
        'COMPLETED', 'CANCELLED', 'REFUNDED', 'DISPUTED'
    ))
);

-- ================================
-- COMMUNICATION FEATURES
-- ================================

-- Chat conversations
CREATE TABLE conversations (
    conversation_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_type VARCHAR(50) DEFAULT 'DIRECT',
    
    -- Participants
    participant_1_id UUID NOT NULL REFERENCES users(user_id),
    participant_2_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Related context
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    order_id UUID REFERENCES orders(order_id),
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_by UUID REFERENCES users(user_id),
    
    last_message_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_conversation_type CHECK (conversation_type IN ('DIRECT', 'GROUP', 'SUPPORT')),
    CONSTRAINT different_participants CHECK (participant_1_id != participant_2_id)
);

-- Chat messages
CREATE TABLE messages (
    message_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(conversation_id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Message content
    message_type VARCHAR(50) DEFAULT 'TEXT',
    content TEXT,
    
    -- Media attachments
    attachments JSONB, -- Images, videos, files
    
    -- Message metadata
    reply_to_message_id UUID REFERENCES messages(message_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    
    -- Read status
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    
    -- Status
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_message_type CHECK (message_type IN ('TEXT', 'IMAGE', 'VIDEO', 'FILE', 'VOICE', 'SYSTEM'))
);

-- Video calls (Azure Communication Services)
CREATE TABLE video_calls (
    call_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(conversation_id),
    
    -- Call participants
    caller_id UUID NOT NULL REFERENCES users(user_id),
    receiver_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Azure Communication Services integration
    acs_call_id VARCHAR(255),
    acs_thread_id VARCHAR(255),
    
    -- Call details
    call_type VARCHAR(50) DEFAULT 'VIDEO',
    call_status VARCHAR(50) DEFAULT 'INITIATED',
    
    -- Timing
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    
    -- Call quality metrics
    quality_rating INTEGER, -- 1-5
    connection_issues JSONB,
    
    end_reason VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_call_type CHECK (call_type IN ('VIDEO', 'AUDIO')),
    CONSTRAINT check_call_status CHECK (call_status IN ('INITIATED', 'RINGING', 'CONNECTED', 'ENDED', 'FAILED', 'CANCELLED')),
    CONSTRAINT check_quality_rating CHECK (quality_rating IS NULL OR (quality_rating >= 1 AND quality_rating <= 5))
);

-- Live streaming sessions
CREATE TABLE live_streams (
    stream_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    streamer_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Stream details
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    
    -- Featured items for sale
    featured_items JSONB, -- Array of item IDs being showcased
    
    -- Stream configuration
    stream_key VARCHAR(255),
    rtmp_url VARCHAR(500),
    playback_url VARCHAR(500),
    
    -- Stream status
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    
    -- Metrics
    viewer_count INTEGER DEFAULT 0,
    max_viewers INTEGER DEFAULT 0,
    total_views INTEGER DEFAULT 0,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    
    -- Schedule
    scheduled_start TIMESTAMP,
    actual_start TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    
    -- Monetization
    sales_during_stream DECIMAL(12,2) DEFAULT 0,
    
    -- Settings
    is_public BOOLEAN DEFAULT TRUE,
    allow_comments BOOLEAN DEFAULT TRUE,
    allow_recording BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_stream_status CHECK (status IN ('SCHEDULED', 'LIVE', 'ENDED', 'CANCELLED'))
);

-- Live stream interactions
CREATE TABLE stream_interactions (
    interaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stream_id UUID NOT NULL REFERENCES live_streams(stream_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    interaction_type VARCHAR(50) NOT NULL,
    content TEXT,
    
    -- Metadata
    metadata JSONB, -- For different interaction types
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_interaction_type CHECK (interaction_type IN ('COMMENT', 'LIKE', 'SHARE', 'PURCHASE', 'GIFT'))
);

-- ================================
-- REVIEWS & RATINGS
-- ================================

-- Reviews (for items, users, transactions)
CREATE TABLE reviews (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reviewer_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Review target (one of these will be set)
    reviewed_user_id UUID REFERENCES users(user_id),
    item_id UUID REFERENCES items(item_id),
    order_id UUID REFERENCES orders(order_id),
    
    -- Review content
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    content TEXT,
    
    -- Review categories (for detailed feedback)
    quality_rating INTEGER,
    communication_rating INTEGER,
    delivery_rating INTEGER,
    value_rating INTEGER,
    
    -- Media
    images JSONB,
    videos JSONB,
    
    -- Status
    is_verified BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_hidden BOOLEAN DEFAULT FALSE,
    
    -- Helpful votes
    helpful_count INTEGER DEFAULT 0,
    not_helpful_count INTEGER DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_rating CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT check_quality_rating CHECK (quality_rating IS NULL OR (quality_rating >= 1 AND quality_rating <= 5)),
    CONSTRAINT check_communication_rating CHECK (communication_rating IS NULL OR (communication_rating >= 1 AND communication_rating <= 5)),
    CONSTRAINT check_delivery_rating CHECK (delivery_rating IS NULL OR (delivery_rating >= 1 AND delivery_rating <= 5)),
    CONSTRAINT check_value_rating CHECK (value_rating IS NULL OR (value_rating >= 1 AND value_rating <= 5)),
    CONSTRAINT review_target_check CHECK (
        (reviewed_user_id IS NOT NULL AND item_id IS NULL AND order_id IS NULL) OR
        (reviewed_user_id IS NULL AND item_id IS NOT NULL AND order_id IS NULL) OR
        (reviewed_user_id IS NULL AND item_id IS NULL AND order_id IS NOT NULL)
    )
);

-- ================================
-- POINTS & REWARDS SYSTEM
-- ================================

-- Point transactions
CREATE TABLE point_transactions (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Transaction details
    transaction_type VARCHAR(50) NOT NULL,
    points_amount INTEGER NOT NULL,
    description TEXT,
    
    -- Related entities
    order_id UUID REFERENCES orders(order_id),
    item_id UUID REFERENCES items(item_id),
    collection_request_id UUID REFERENCES collection_requests(request_id),
    
    -- Balance tracking
    balance_before INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    
    -- Expiration (for earned points)
    expires_at TIMESTAMP,
    
    -- Status
    status VARCHAR(50) DEFAULT 'COMPLETED',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_transaction_type CHECK (transaction_type IN (
        'EARNED_COLLECTION', 'EARNED_PURCHASE', 'EARNED_REVIEW', 'EARNED_REFERRAL',
        'SPENT_DISCOUNT', 'SPENT_PREMIUM', 'EXPIRED', 'ADJUSTMENT'
    )),
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'EXPIRED'))
);

-- Promotions and rewards
CREATE TABLE promotions (
    promotion_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Promotion details
    name VARCHAR(255) NOT NULL,
    description TEXT,
    promotion_type VARCHAR(50) NOT NULL,
    
    -- Discount configuration
    discount_type VARCHAR(50), -- PERCENTAGE, FIXED_AMOUNT, POINTS
    discount_value DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    
    -- Usage limits
    max_uses INTEGER,
    max_uses_per_user INTEGER DEFAULT 1,
    current_uses INTEGER DEFAULT 0,
    
    -- Eligibility
    eligible_user_types JSONB,
    eligible_categories JSONB,
    eligible_brands JSONB,
    
    -- Validity
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_promotion_type CHECK (promotion_type IN ('DISCOUNT', 'POINTS_MULTIPLIER', 'FREE_SHIPPING', 'GIFT')),
    CONSTRAINT check_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'POINTS') OR discount_type IS NULL)
);

-- ================================
-- REPORTS & MODERATION
-- ================================

-- User reports
CREATE TABLE reports (
    report_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Report target
    reported_user_id UUID REFERENCES users(user_id),
    reported_item_id UUID REFERENCES items(item_id),
    reported_listing_id UUID REFERENCES marketplace_listings(listing_id),
    reported_message_id UUID REFERENCES messages(message_id),
    
    -- Report details
    report_type VARCHAR(50) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    evidence JSONB, -- Screenshots, links, etc.
    
    -- Resolution
    status VARCHAR(50) DEFAULT 'PENDING',
    assigned_to UUID REFERENCES users(user_id),
    resolution TEXT,
    action_taken VARCHAR(255),
    resolved_at TIMESTAMP,
    resolved_by UUID REFERENCES users(user_id),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_report_type CHECK (report_type IN (
        'SPAM', 'FRAUD', 'INAPPROPRIATE_CONTENT', 'FAKE_ITEM', 'HARASSMENT', 
        'COPYRIGHT', 'SAFETY', 'OTHER'
    )),
    CONSTRAINT check_report_status CHECK (status IN ('PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED'))
);

-- ================================
-- ANALYTICS & METRICS
-- ================================

-- User activity tracking
CREATE TABLE user_activities (
    activity_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(user_id),
    
    activity_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    
    -- Activity metadata
    metadata JSONB,
    ip_address INET,
    user_agent TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_activity_type CHECK (activity_type IN (
        'LOGIN', 'LOGOUT', 'VIEW_ITEM', 'SEARCH', 'ADD_TO_FAVORITES', 
        'CREATE_LISTING', 'PURCHASE', 'MESSAGE_SENT', 'PROFILE_UPDATE', 'REVIEW_CREATED'
    ))
);

-- ================================
-- NOTIFICATION SYSTEM
-- ================================

-- Notifications
CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Notification content
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    
    -- Related entities
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    
    -- Action URL
    action_url VARCHAR(500),
    
    -- Status
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    
    -- Delivery
    delivery_method VARCHAR(50) DEFAULT 'IN_APP',
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_notification_type CHECK (type IN (
        'ORDER_UPDATE', 'MESSAGE_RECEIVED', 'ITEM_SOLD', 'ITEM_REVIEWED', 
        'COLLECTION_SCHEDULED', 'POINTS_EARNED', 'PROMOTION_AVAILABLE', 
        'SYSTEM_ANNOUNCEMENT', 'ACCOUNT_UPDATE'
    )),
    CONSTRAINT check_delivery_method CHECK (delivery_method IN ('IN_APP', 'EMAIL', 'SMS', 'PUSH'))
);

-- ================================
-- SYSTEM CONFIGURATION
-- ================================

-- System settings
CREATE TABLE system_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    setting_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    updated_by UUID REFERENCES users(user_id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_setting_type CHECK (setting_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON'))
);

-- ================================
-- PERFORMANCE INDEXES
-- ================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_google_id ON users(google_id);
CREATE INDEX idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Address indexes
CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_addresses_is_default ON user_addresses(is_default);
CREATE INDEX idx_user_addresses_coordinates ON user_addresses(latitude, longitude);

-- Item indexes
CREATE INDEX idx_items_item_code ON items(item_code);
CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_items_brand_id ON items(brand_id);
CREATE INDEX idx_items_current_owner_id ON items(current_owner_id);
CREATE INDEX idx_items_item_status ON items(item_status);
CREATE INDEX idx_items_condition_score ON items(condition_score);
CREATE INDEX idx_items_created_at ON items(created_at);

-- Marketplace indexes
CREATE INDEX idx_marketplace_listings_seller_id ON marketplace_listings(seller_id);
CREATE INDEX idx_marketplace_listings_item_id ON marketplace_listings(item_id);
CREATE INDEX idx_marketplace_listings_status ON marketplace_listings(status);
CREATE INDEX idx_marketplace_listings_listing_type ON marketplace_listings(listing_type);
CREATE INDEX idx_marketplace_listings_price ON marketplace_listings(price);
CREATE INDEX idx_marketplace_listings_created_at ON marketplace_listings(created_at);

-- Order indexes
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Communication indexes
CREATE INDEX idx_conversations_participants ON conversations(participant_1_id, participant_2_id);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- Collection indexes
CREATE INDEX idx_collection_requests_user_id ON collection_requests(user_id);
CREATE INDEX idx_collection_requests_status ON collection_requests(status);
CREATE INDEX idx_collection_requests_preferred_date ON collection_requests(preferred_date);

-- Point transaction indexes
CREATE INDEX idx_point_transactions_user_id ON point_transactions(user_id);
CREATE INDEX idx_point_transactions_type ON point_transactions(transaction_type);
CREATE INDEX idx_point_transactions_created_at ON point_transactions(created_at);

-- Review indexes
CREATE INDEX idx_reviews_reviewer_id ON reviews(reviewer_id);
CREATE INDEX idx_reviews_reviewed_user_id ON reviews(reviewed_user_id);
CREATE INDEX idx_reviews_item_id ON reviews(item_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Notification indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- Activity indexes
CREATE INDEX idx_user_activities_user_id ON user_activities(user_id);
CREATE INDEX idx_user_activities_type ON user_activities(activity_type);
CREATE INDEX idx_user_activities_created_at ON user_activities(created_at);

-- GIN indexes for JSONB columns (for fast searches)
CREATE INDEX idx_items_images_gin ON items USING GIN(images);
CREATE INDEX idx_items_tags_gin ON items USING GIN(tags);
CREATE INDEX idx_items_metadata_gin ON items USING GIN(metadata);
CREATE INDEX idx_marketplace_listings_tags_gin ON marketplace_listings USING GIN(tags);

-- Text search indexes
CREATE INDEX idx_items_search ON items USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, '')));
CREATE INDEX idx_marketplace_listings_search ON marketplace_listings USING GIN(to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- ================================
-- INITIAL DATA
-- ================================

-- Insert default system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description, is_public) VALUES
('platform_name', 'GreenLoop', 'STRING', 'Platform display name', true),
('platform_version', '1.0.0', 'STRING', 'Current platform version', true),
('max_file_size_mb', '10', 'INTEGER', 'Maximum file upload size in MB', false),
('points_per_collection', '100', 'INTEGER', 'Points awarded per successful collection', false),
('min_payout_amount', '100000', 'INTEGER', 'Minimum amount for withdrawal (VND)', false),
('service_fee_percentage', '5', 'DECIMAL', 'Platform service fee percentage', false),
('enable_live_streaming', 'true', 'BOOLEAN', 'Enable live streaming features', true),
('enable_video_calls', 'true', 'BOOLEAN', 'Enable video call features', true);

-- Insert default categories
INSERT INTO categories (name, slug, description, display_order, is_active) VALUES
('Áo', 'ao', 'Các loại áo quần áo', 1, true),
('Quần', 'quan', 'Các loại quần', 2, true),
('Giày dép', 'giay-dep', 'Giày, dép, sandal', 3, true),
('Phụ kiện', 'phu-kien', 'Túi xách, ví, thắt lưng, jewelry', 4, true),
('Đầm váy', 'dam-vay', 'Đầm, váy, jumpsuit', 5, true),
('Đồ thể thao', 'do-the-thao', 'Quần áo và phụ kiện thể thao', 6, true),
('Đồ trẻ em', 'do-tre-em', 'Quần áo trẻ em và phụ kiện', 7, true);

-- Insert some popular brands
INSERT INTO brands (name, slug, description, sustainability_rating, is_verified) VALUES
('Zara', 'zara', 'Fast fashion retailer', 2.5, true),
('H&M', 'hm', 'Swedish clothing retailer', 3.0, true),
('Uniqlo', 'uniqlo', 'Japanese casual wear designer', 3.5, true),
('Local Brand', 'local-brand', 'Vietnamese local fashion brands', 4.0, true),
('Eco Fashion', 'eco-fashion', 'Sustainable fashion brands', 5.0, true);

COMMIT; 
-- Version 1.0 - Initial Schema

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ================================
-- CORE USER MANAGEMENT
-- ================================

-- Users table (enhanced for recycling platform)
CREATE TABLE users (
    user_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    username VARCHAR(100) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    
    -- Role and permissions
    user_type VARCHAR(50) NOT NULL DEFAULT 'CONSUMER',
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    
    -- Profile information
    avatar_url VARCHAR(500),
    bio TEXT,
    
    -- Points and scoring
    sustainability_points INTEGER DEFAULT 0,
    sustainability_score DECIMAL(5,2) DEFAULT 0.0,
    trust_score DECIMAL(5,2) DEFAULT 5.0,
    
    -- Account status
    is_verified BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    is_banned BOOLEAN DEFAULT FALSE,
    
    -- Authentication
    google_id VARCHAR(255),
    firebase_uid VARCHAR(255),
    last_login TIMESTAMP,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_user_type CHECK (user_type IN ('CONSUMER', 'COLLECTOR', 'BRAND', 'ADMIN', 'MODERATOR')),
    CONSTRAINT check_role CHECK (role IN ('USER', 'PREMIUM', 'COLLECTOR', 'ADMIN', 'SUPER_ADMIN')),
    CONSTRAINT check_trust_score CHECK (trust_score >= 0 AND trust_score <= 10)
);

-- User addresses (supports multiple addresses)
CREATE TABLE user_addresses (
    address_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    
    address_type VARCHAR(50) NOT NULL,
    label VARCHAR(100),
    
    -- Address details (Vietnam-focused)
    street_address TEXT NOT NULL,
    ward VARCHAR(100),
    district VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Vietnam',
    
    -- Coordinates for collection routing
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    is_default BOOLEAN DEFAULT FALSE,
    is_collection_point BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_address_type CHECK (address_type IN ('HOME', 'WORK', 'COLLECTION_POINT', 'OTHER'))
);

-- User follows (social features)
CREATE TABLE user_follows (
    follow_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    follower_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    followed_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(follower_id, followed_id),
    CONSTRAINT no_self_follow CHECK (follower_id != followed_id)
);

-- ================================
-- PRODUCT CATALOG
-- ================================

-- Categories for fashion items
CREATE TABLE categories (
    category_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    parent_category_id UUID REFERENCES categories(category_id),
    description TEXT,
    image_url VARCHAR(500),
    icon VARCHAR(100),
    
    -- Category specific settings
    requires_authentication BOOLEAN DEFAULT FALSE,
    min_condition_score DECIMAL(3,2) DEFAULT 1.0,
    
    display_order INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Brands
CREATE TABLE brands (
    brand_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    logo_url VARCHAR(500),
    website VARCHAR(255),
    
    -- Sustainability metrics
    sustainability_rating DECIMAL(3,2) DEFAULT 0.0,
    eco_certification JSONB,
    
    -- Status
    is_verified BOOLEAN DEFAULT FALSE,
    is_partner BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_sustainability_rating CHECK (sustainability_rating >= 0 AND sustainability_rating <= 5)
);

-- ================================
-- ITEM MANAGEMENT & LIFECYCLE
-- ================================

-- Core items table (fashion items in the system)
CREATE TABLE items (
    item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_code VARCHAR(50) UNIQUE NOT NULL, -- QR/Barcode for tracking
    
    -- Basic information
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id UUID NOT NULL REFERENCES categories(category_id),
    brand_id UUID REFERENCES brands(brand_id),
    
    -- Physical properties
    size VARCHAR(50),
    color VARCHAR(100),
    material_composition JSONB, -- {"cotton": 70, "polyester": 30}
    weight_grams INTEGER,
    dimensions JSONB, -- {"length": 50, "width": 40, "height": 2}
    
    -- Condition and valuation
    condition_score DECIMAL(3,2) NOT NULL, -- 1.0 to 5.0
    condition_description TEXT,
    original_price DECIMAL(12,2),
    current_estimated_value DECIMAL(12,2),
    
    -- Ownership and lifecycle
    original_owner_id UUID REFERENCES users(user_id),
    current_owner_id UUID REFERENCES users(user_id),
    acquisition_method VARCHAR(50) NOT NULL, -- COLLECTED, PURCHASED, TRADED, etc.
    
    -- Item status in system
    item_status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED',
    is_verified BOOLEAN DEFAULT FALSE,
    verification_date TIMESTAMP,
    verified_by UUID REFERENCES users(user_id),
    
    -- Sustainability data
    carbon_footprint_kg DECIMAL(10,4),
    water_saved_liters DECIMAL(10,2),
    energy_saved_kwh DECIMAL(10,2),
    
    -- Media
    images JSONB, -- Array of image URLs
    videos JSONB, -- Array of video URLs
    
    -- Metadata
    tags JSONB, -- Searchable tags
    metadata JSONB, -- Additional flexible data
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_condition_score CHECK (condition_score >= 1.0 AND condition_score <= 5.0),
    CONSTRAINT check_item_status CHECK (item_status IN (
        'SUBMITTED', 'PENDING_COLLECTION', 'COLLECTED', 'VALUING', 'VALUED', 
        'PROCESSING', 'READY_FOR_SALE', 'LISTED', 'SOLD', 'RENTED', 'DONATED', 
        'RECYCLED', 'REJECTED'
    )),
    CONSTRAINT check_acquisition_method CHECK (acquisition_method IN (
        'COLLECTED', 'PURCHASED', 'TRADED', 'DONATED', 'IMPORTED'
    ))
);

-- Item lifecycle history (tracks all changes)
CREATE TABLE item_lifecycle (
    lifecycle_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(item_id) ON DELETE CASCADE,
    
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    previous_owner_id UUID REFERENCES users(user_id),
    new_owner_id UUID REFERENCES users(user_id),
    
    change_reason VARCHAR(255),
    notes TEXT,
    
    -- Environmental impact of this change
    carbon_impact_kg DECIMAL(10,4),
    energy_impact_kwh DECIMAL(10,2),
    
    changed_by UUID REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- COLLECTION SYSTEM
-- ================================

-- Collection points (physical locations)
CREATE TABLE collection_points (
    point_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    point_type VARCHAR(50) NOT NULL,
    
    -- Location
    address_id UUID NOT NULL REFERENCES user_addresses(address_id),
    
    -- Capacity and status
    max_capacity INTEGER DEFAULT 100,
    current_capacity INTEGER DEFAULT 0,
    
    -- Operating information
    operating_hours JSONB, -- {"monday": {"open": "08:00", "close": "18:00"}}
    contact_info JSONB,
    
    -- IoT integration ready
    iot_device_id VARCHAR(100),
    sensor_data JSONB,
    
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_point_type CHECK (point_type IN ('RESIDENTIAL', 'PUBLIC', 'COMMERCIAL', 'PARTNER'))
);

-- Collection requests from users
CREATE TABLE collection_requests (
    request_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Collection details
    pickup_address_id UUID NOT NULL REFERENCES user_addresses(address_id),
    preferred_date DATE,
    preferred_time_slot VARCHAR(50),
    
    -- Items to be collected
    estimated_items_count INTEGER,
    item_categories JSONB, -- Categories of items to collect
    special_instructions TEXT,
    
    -- Request status
    status VARCHAR(50) DEFAULT 'PENDING',
    priority_level INTEGER DEFAULT 3, -- 1-5, 5 is highest
    
    -- Assignment
    assigned_collector_id UUID REFERENCES users(user_id),
    collection_point_id UUID REFERENCES collection_points(point_id),
    
    -- Completion
    collected_at TIMESTAMP,
    collector_notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT check_priority CHECK (priority_level >= 1 AND priority_level <= 5)
);

-- Collection request items (items submitted for collection)
CREATE TABLE collection_request_items (
    request_item_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    collection_request_id UUID NOT NULL REFERENCES collection_requests(request_id) ON DELETE CASCADE,
    item_id UUID REFERENCES items(item_id), -- Linked after valuation
    
    -- User-provided information
    user_description TEXT,
    user_estimated_value DECIMAL(10,2),
    user_images JSONB,
    
    -- Valuation results
    valuator_id UUID REFERENCES users(user_id),
    final_valuation DECIMAL(10,2),
    valuation_notes TEXT,
    valuation_date TIMESTAMP,
    
    -- Points awarded
    points_awarded INTEGER DEFAULT 0,
    
    status VARCHAR(50) DEFAULT 'PENDING',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_item_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'PROCESSING'))
);

-- ================================
-- MARKETPLACE
-- ================================

-- Marketplace listings
CREATE TABLE marketplace_listings (
    listing_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    item_id UUID NOT NULL REFERENCES items(item_id),
    seller_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Listing details
    title VARCHAR(255) NOT NULL,
    description TEXT,
    listing_type VARCHAR(50) NOT NULL,
    
    -- Pricing
    price DECIMAL(12,2),
    rental_price_per_day DECIMAL(10,2),
    rental_price_per_week DECIMAL(10,2),
    rental_price_per_month DECIMAL(10,2),
    original_price DECIMAL(12,2),
    
    -- Trading
    accepts_trades BOOLEAN DEFAULT FALSE,
    preferred_trade_items TEXT,
    
    -- Availability
    available_from DATE DEFAULT CURRENT_DATE,
    available_until DATE,
    quantity_available INTEGER DEFAULT 1,
    min_rental_days INTEGER DEFAULT 1,
    max_rental_days INTEGER DEFAULT 30,
    
    -- Location and delivery
    pickup_location_id UUID REFERENCES user_addresses(address_id),
    delivery_available BOOLEAN DEFAULT FALSE,
    delivery_fee DECIMAL(10,2),
    delivery_radius_km INTEGER,
    
    -- Status and visibility
    status VARCHAR(50) DEFAULT 'DRAFT',
    is_featured BOOLEAN DEFAULT FALSE,
    boost_expires_at TIMESTAMP,
    
    -- Metrics
    view_count INTEGER DEFAULT 0,
    favorite_count INTEGER DEFAULT 0,
    inquiry_count INTEGER DEFAULT 0,
    
    -- SEO and searchability
    tags JSONB,
    keywords VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_listing_type CHECK (listing_type IN ('SELL', 'RENT', 'TRADE', 'FREE')),
    CONSTRAINT check_status CHECK (status IN ('DRAFT', 'ACTIVE', 'PAUSED', 'SOLD', 'RENTED', 'EXPIRED', 'REMOVED'))
);

-- ================================
-- TRANSACTIONS & ORDERS
-- ================================

-- Orders/Transactions
CREATE TABLE orders (
    order_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number VARCHAR(50) UNIQUE NOT NULL,
    
    buyer_id UUID NOT NULL REFERENCES users(user_id),
    seller_id UUID NOT NULL REFERENCES users(user_id),
    listing_id UUID NOT NULL REFERENCES marketplace_listings(listing_id),
    item_id UUID NOT NULL REFERENCES items(item_id),
    
    -- Order details
    order_type VARCHAR(50) NOT NULL,
    quantity INTEGER DEFAULT 1,
    
    -- Pricing breakdown
    item_price DECIMAL(12,2),
    delivery_fee DECIMAL(10,2) DEFAULT 0,
    service_fee DECIMAL(10,2) DEFAULT 0,
    tax_amount DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    
    -- Rental specific
    rental_start_date DATE,
    rental_end_date DATE,
    rental_days INTEGER,
    security_deposit DECIMAL(12,2),
    
    -- Payment
    payment_method VARCHAR(50),
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_id VARCHAR(255), -- External payment provider ID
    
    -- Delivery/Pickup
    delivery_method VARCHAR(50),
    delivery_address_id UUID REFERENCES user_addresses(address_id),
    tracking_number VARCHAR(100),
    
    -- Status
    order_status VARCHAR(50) DEFAULT 'PENDING',
    
    -- Special instructions
    buyer_notes TEXT,
    seller_notes TEXT,
    admin_notes TEXT,
    
    -- Important dates
    confirmed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_order_type CHECK (order_type IN ('PURCHASE', 'RENTAL', 'TRADE')),
    CONSTRAINT check_payment_status CHECK (payment_status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    CONSTRAINT check_order_status CHECK (order_status IN (
        'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 
        'COMPLETED', 'CANCELLED', 'REFUNDED', 'DISPUTED'
    ))
);

-- ================================
-- COMMUNICATION FEATURES
-- ================================

-- Chat conversations
CREATE TABLE conversations (
    conversation_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_type VARCHAR(50) DEFAULT 'DIRECT',
    
    -- Participants
    participant_1_id UUID NOT NULL REFERENCES users(user_id),
    participant_2_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Related context
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    order_id UUID REFERENCES orders(order_id),
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_by UUID REFERENCES users(user_id),
    
    last_message_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_conversation_type CHECK (conversation_type IN ('DIRECT', 'GROUP', 'SUPPORT')),
    CONSTRAINT different_participants CHECK (participant_1_id != participant_2_id)
);

-- Chat messages
CREATE TABLE messages (
    message_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(conversation_id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Message content
    message_type VARCHAR(50) DEFAULT 'TEXT',
    content TEXT,
    
    -- Media attachments
    attachments JSONB, -- Images, videos, files
    
    -- Message metadata
    reply_to_message_id UUID REFERENCES messages(message_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    
    -- Read status
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    
    -- Status
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_message_type CHECK (message_type IN ('TEXT', 'IMAGE', 'VIDEO', 'FILE', 'VOICE', 'SYSTEM'))
);

-- Video calls (Azure Communication Services)
CREATE TABLE video_calls (
    call_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    conversation_id UUID NOT NULL REFERENCES conversations(conversation_id),
    
    -- Call participants
    caller_id UUID NOT NULL REFERENCES users(user_id),
    receiver_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Azure Communication Services integration
    acs_call_id VARCHAR(255),
    acs_thread_id VARCHAR(255),
    
    -- Call details
    call_type VARCHAR(50) DEFAULT 'VIDEO',
    call_status VARCHAR(50) DEFAULT 'INITIATED',
    
    -- Timing
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    
    -- Call quality metrics
    quality_rating INTEGER, -- 1-5
    connection_issues JSONB,
    
    end_reason VARCHAR(100),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_call_type CHECK (call_type IN ('VIDEO', 'AUDIO')),
    CONSTRAINT check_call_status CHECK (call_status IN ('INITIATED', 'RINGING', 'CONNECTED', 'ENDED', 'FAILED', 'CANCELLED')),
    CONSTRAINT check_quality_rating CHECK (quality_rating IS NULL OR (quality_rating >= 1 AND quality_rating <= 5))
);

-- Live streaming sessions
CREATE TABLE live_streams (
    stream_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    streamer_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Stream details
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    
    -- Featured items for sale
    featured_items JSONB, -- Array of item IDs being showcased
    
    -- Stream configuration
    stream_key VARCHAR(255),
    rtmp_url VARCHAR(500),
    playback_url VARCHAR(500),
    
    -- Stream status
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    
    -- Metrics
    viewer_count INTEGER DEFAULT 0,
    max_viewers INTEGER DEFAULT 0,
    total_views INTEGER DEFAULT 0,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    
    -- Schedule
    scheduled_start TIMESTAMP,
    actual_start TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    
    -- Monetization
    sales_during_stream DECIMAL(12,2) DEFAULT 0,
    
    -- Settings
    is_public BOOLEAN DEFAULT TRUE,
    allow_comments BOOLEAN DEFAULT TRUE,
    allow_recording BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_stream_status CHECK (status IN ('SCHEDULED', 'LIVE', 'ENDED', 'CANCELLED'))
);

-- Live stream interactions
CREATE TABLE stream_interactions (
    interaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    stream_id UUID NOT NULL REFERENCES live_streams(stream_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    interaction_type VARCHAR(50) NOT NULL,
    content TEXT,
    
    -- Metadata
    metadata JSONB, -- For different interaction types
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_interaction_type CHECK (interaction_type IN ('COMMENT', 'LIKE', 'SHARE', 'PURCHASE', 'GIFT'))
);

-- ================================
-- REVIEWS & RATINGS
-- ================================

-- Reviews (for items, users, transactions)
CREATE TABLE reviews (
    review_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reviewer_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Review target (one of these will be set)
    reviewed_user_id UUID REFERENCES users(user_id),
    item_id UUID REFERENCES items(item_id),
    order_id UUID REFERENCES orders(order_id),
    
    -- Review content
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    content TEXT,
    
    -- Review categories (for detailed feedback)
    quality_rating INTEGER,
    communication_rating INTEGER,
    delivery_rating INTEGER,
    value_rating INTEGER,
    
    -- Media
    images JSONB,
    videos JSONB,
    
    -- Status
    is_verified BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_hidden BOOLEAN DEFAULT FALSE,
    
    -- Helpful votes
    helpful_count INTEGER DEFAULT 0,
    not_helpful_count INTEGER DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_rating CHECK (rating >= 1 AND rating <= 5),
    CONSTRAINT check_quality_rating CHECK (quality_rating IS NULL OR (quality_rating >= 1 AND quality_rating <= 5)),
    CONSTRAINT check_communication_rating CHECK (communication_rating IS NULL OR (communication_rating >= 1 AND communication_rating <= 5)),
    CONSTRAINT check_delivery_rating CHECK (delivery_rating IS NULL OR (delivery_rating >= 1 AND delivery_rating <= 5)),
    CONSTRAINT check_value_rating CHECK (value_rating IS NULL OR (value_rating >= 1 AND value_rating <= 5)),
    CONSTRAINT review_target_check CHECK (
        (reviewed_user_id IS NOT NULL AND item_id IS NULL AND order_id IS NULL) OR
        (reviewed_user_id IS NULL AND item_id IS NOT NULL AND order_id IS NULL) OR
        (reviewed_user_id IS NULL AND item_id IS NULL AND order_id IS NOT NULL)
    )
);

-- ================================
-- POINTS & REWARDS SYSTEM
-- ================================

-- Point transactions
CREATE TABLE point_transactions (
    transaction_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Transaction details
    transaction_type VARCHAR(50) NOT NULL,
    points_amount INTEGER NOT NULL,
    description TEXT,
    
    -- Related entities
    order_id UUID REFERENCES orders(order_id),
    item_id UUID REFERENCES items(item_id),
    collection_request_id UUID REFERENCES collection_requests(request_id),
    
    -- Balance tracking
    balance_before INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    
    -- Expiration (for earned points)
    expires_at TIMESTAMP,
    
    -- Status
    status VARCHAR(50) DEFAULT 'COMPLETED',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_transaction_type CHECK (transaction_type IN (
        'EARNED_COLLECTION', 'EARNED_PURCHASE', 'EARNED_REVIEW', 'EARNED_REFERRAL',
        'SPENT_DISCOUNT', 'SPENT_PREMIUM', 'EXPIRED', 'ADJUSTMENT'
    )),
    CONSTRAINT check_status CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'EXPIRED'))
);

-- Promotions and rewards
CREATE TABLE promotions (
    promotion_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Promotion details
    name VARCHAR(255) NOT NULL,
    description TEXT,
    promotion_type VARCHAR(50) NOT NULL,
    
    -- Discount configuration
    discount_type VARCHAR(50), -- PERCENTAGE, FIXED_AMOUNT, POINTS
    discount_value DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    
    -- Usage limits
    max_uses INTEGER,
    max_uses_per_user INTEGER DEFAULT 1,
    current_uses INTEGER DEFAULT 0,
    
    -- Eligibility
    eligible_user_types JSONB,
    eligible_categories JSONB,
    eligible_brands JSONB,
    
    -- Validity
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_promotion_type CHECK (promotion_type IN ('DISCOUNT', 'POINTS_MULTIPLIER', 'FREE_SHIPPING', 'GIFT')),
    CONSTRAINT check_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'POINTS') OR discount_type IS NULL)
);

-- ================================
-- REPORTS & MODERATION
-- ================================

-- User reports
CREATE TABLE reports (
    report_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reporter_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Report target
    reported_user_id UUID REFERENCES users(user_id),
    reported_item_id UUID REFERENCES items(item_id),
    reported_listing_id UUID REFERENCES marketplace_listings(listing_id),
    reported_message_id UUID REFERENCES messages(message_id),
    
    -- Report details
    report_type VARCHAR(50) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    description TEXT,
    evidence JSONB, -- Screenshots, links, etc.
    
    -- Resolution
    status VARCHAR(50) DEFAULT 'PENDING',
    assigned_to UUID REFERENCES users(user_id),
    resolution TEXT,
    action_taken VARCHAR(255),
    resolved_at TIMESTAMP,
    resolved_by UUID REFERENCES users(user_id),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_report_type CHECK (report_type IN (
        'SPAM', 'FRAUD', 'INAPPROPRIATE_CONTENT', 'FAKE_ITEM', 'HARASSMENT', 
        'COPYRIGHT', 'SAFETY', 'OTHER'
    )),
    CONSTRAINT check_report_status CHECK (status IN ('PENDING', 'INVESTIGATING', 'RESOLVED', 'DISMISSED'))
);

-- ================================
-- ANALYTICS & METRICS
-- ================================

-- User activity tracking
CREATE TABLE user_activities (
    activity_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(user_id),
    
    activity_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id UUID,
    
    -- Activity metadata
    metadata JSONB,
    ip_address INET,
    user_agent TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_activity_type CHECK (activity_type IN (
        'LOGIN', 'LOGOUT', 'VIEW_ITEM', 'SEARCH', 'ADD_TO_FAVORITES', 
        'CREATE_LISTING', 'PURCHASE', 'MESSAGE_SENT', 'PROFILE_UPDATE', 'REVIEW_CREATED'
    ))
);

-- ================================
-- NOTIFICATION SYSTEM
-- ================================

-- Notifications
CREATE TABLE notifications (
    notification_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id),
    
    -- Notification content
    type VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    
    -- Related entities
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    
    -- Action URL
    action_url VARCHAR(500),
    
    -- Status
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    
    -- Delivery
    delivery_method VARCHAR(50) DEFAULT 'IN_APP',
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_notification_type CHECK (type IN (
        'ORDER_UPDATE', 'MESSAGE_RECEIVED', 'ITEM_SOLD', 'ITEM_REVIEWED', 
        'COLLECTION_SCHEDULED', 'POINTS_EARNED', 'PROMOTION_AVAILABLE', 
        'SYSTEM_ANNOUNCEMENT', 'ACCOUNT_UPDATE'
    )),
    CONSTRAINT check_delivery_method CHECK (delivery_method IN ('IN_APP', 'EMAIL', 'SMS', 'PUSH'))
);

-- ================================
-- SYSTEM CONFIGURATION
-- ================================

-- System settings
CREATE TABLE system_settings (
    setting_key VARCHAR(100) PRIMARY KEY,
    setting_value TEXT NOT NULL,
    setting_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_public BOOLEAN DEFAULT FALSE,
    updated_by UUID REFERENCES users(user_id),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_setting_type CHECK (setting_type IN ('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON'))
);

-- ================================
-- PERFORMANCE INDEXES
-- ================================

-- User indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_google_id ON users(google_id);
CREATE INDEX idx_users_firebase_uid ON users(firebase_uid);
CREATE INDEX idx_users_user_type ON users(user_type);
CREATE INDEX idx_users_is_active ON users(is_active);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Address indexes
CREATE INDEX idx_user_addresses_user_id ON user_addresses(user_id);
CREATE INDEX idx_user_addresses_is_default ON user_addresses(is_default);
CREATE INDEX idx_user_addresses_coordinates ON user_addresses(latitude, longitude);

-- Item indexes
CREATE INDEX idx_items_item_code ON items(item_code);
CREATE INDEX idx_items_category_id ON items(category_id);
CREATE INDEX idx_items_brand_id ON items(brand_id);
CREATE INDEX idx_items_current_owner_id ON items(current_owner_id);
CREATE INDEX idx_items_item_status ON items(item_status);
CREATE INDEX idx_items_condition_score ON items(condition_score);
CREATE INDEX idx_items_created_at ON items(created_at);

-- Marketplace indexes
CREATE INDEX idx_marketplace_listings_seller_id ON marketplace_listings(seller_id);
CREATE INDEX idx_marketplace_listings_item_id ON marketplace_listings(item_id);
CREATE INDEX idx_marketplace_listings_status ON marketplace_listings(status);
CREATE INDEX idx_marketplace_listings_listing_type ON marketplace_listings(listing_type);
CREATE INDEX idx_marketplace_listings_price ON marketplace_listings(price);
CREATE INDEX idx_marketplace_listings_created_at ON marketplace_listings(created_at);

-- Order indexes
CREATE INDEX idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX idx_orders_seller_id ON orders(seller_id);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Communication indexes
CREATE INDEX idx_conversations_participants ON conversations(participant_1_id, participant_2_id);
CREATE INDEX idx_messages_conversation_id ON messages(conversation_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);

-- Collection indexes
CREATE INDEX idx_collection_requests_user_id ON collection_requests(user_id);
CREATE INDEX idx_collection_requests_status ON collection_requests(status);
CREATE INDEX idx_collection_requests_preferred_date ON collection_requests(preferred_date);

-- Point transaction indexes
CREATE INDEX idx_point_transactions_user_id ON point_transactions(user_id);
CREATE INDEX idx_point_transactions_type ON point_transactions(transaction_type);
CREATE INDEX idx_point_transactions_created_at ON point_transactions(created_at);

-- Review indexes
CREATE INDEX idx_reviews_reviewer_id ON reviews(reviewer_id);
CREATE INDEX idx_reviews_reviewed_user_id ON reviews(reviewed_user_id);
CREATE INDEX idx_reviews_item_id ON reviews(item_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- Notification indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);

-- Activity indexes
CREATE INDEX idx_user_activities_user_id ON user_activities(user_id);
CREATE INDEX idx_user_activities_type ON user_activities(activity_type);
CREATE INDEX idx_user_activities_created_at ON user_activities(created_at);

-- GIN indexes for JSONB columns (for fast searches)
CREATE INDEX idx_items_images_gin ON items USING GIN(images);
CREATE INDEX idx_items_tags_gin ON items USING GIN(tags);
CREATE INDEX idx_items_metadata_gin ON items USING GIN(metadata);
CREATE INDEX idx_marketplace_listings_tags_gin ON marketplace_listings USING GIN(tags);

-- Text search indexes
CREATE INDEX idx_items_search ON items USING GIN(to_tsvector('english', name || ' ' || COALESCE(description, '')));
CREATE INDEX idx_marketplace_listings_search ON marketplace_listings USING GIN(to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- ================================
-- INITIAL DATA
-- ================================

-- Insert default system settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description, is_public) VALUES
('platform_name', 'GreenLoop', 'STRING', 'Platform display name', true),
('platform_version', '1.0.0', 'STRING', 'Current platform version', true),
('max_file_size_mb', '10', 'INTEGER', 'Maximum file upload size in MB', false),
('points_per_collection', '100', 'INTEGER', 'Points awarded per successful collection', false),
('min_payout_amount', '100000', 'INTEGER', 'Minimum amount for withdrawal (VND)', false),
('service_fee_percentage', '5', 'DECIMAL', 'Platform service fee percentage', false),
('enable_live_streaming', 'true', 'BOOLEAN', 'Enable live streaming features', true),
('enable_video_calls', 'true', 'BOOLEAN', 'Enable video call features', true);

-- Insert default categories
INSERT INTO categories (name, slug, description, display_order, is_active) VALUES
('Áo', 'ao', 'Các loại áo quần áo', 1, true),
('Quần', 'quan', 'Các loại quần', 2, true),
('Giày dép', 'giay-dep', 'Giày, dép, sandal', 3, true),
('Phụ kiện', 'phu-kien', 'Túi xách, ví, thắt lưng, jewelry', 4, true),
('Đầm váy', 'dam-vay', 'Đầm, váy, jumpsuit', 5, true),
('Đồ thể thao', 'do-the-thao', 'Quần áo và phụ kiện thể thao', 6, true),
('Đồ trẻ em', 'do-tre-em', 'Quần áo trẻ em và phụ kiện', 7, true);

-- Insert some popular brands
INSERT INTO brands (name, slug, description, sustainability_rating, is_verified) VALUES
('Zara', 'zara', 'Fast fashion retailer', 2.5, true),
('H&M', 'hm', 'Swedish clothing retailer', 3.0, true),
('Uniqlo', 'uniqlo', 'Japanese casual wear designer', 3.5, true),
('Local Brand', 'local-brand', 'Vietnamese local fashion brands', 4.0, true),
('Eco Fashion', 'eco-fashion', 'Sustainable fashion brands', 5.0, true);

COMMIT; 