-- Social Features Migration
-- Version 2.0 - Add missing tables from ERD

-- ================================
-- ROLE MANAGEMENT
-- ================================

CREATE TABLE role (
    role_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    permissions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles junction table
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID REFERENCES users(user_id),
    PRIMARY KEY (user_id, role_id)
);

-- ================================
-- SOCIAL FEATURES
-- ================================

-- Posts table
CREATE TABLE posts (
    post_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT,
    images JSONB,
    videos JSONB,
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    post_type VARCHAR(50) DEFAULT 'GENERAL',
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    shares_count INTEGER DEFAULT 0,
    views_count INTEGER DEFAULT 0,
    hashtags JSONB,
    visibility VARCHAR(50) DEFAULT 'PUBLIC',
    is_featured BOOLEAN DEFAULT FALSE,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_post_type CHECK (post_type IN (
        'GENERAL', 'OUTFIT', 'STYLING_TIP', 'SUSTAINABILITY_TIP', 'ITEM_SHOWCASE',
        'BEFORE_AFTER', 'RECYCLING_STORY', 'BRAND_REVIEW', 'LIVE_STREAM_ANNOUNCEMENT'
    )),
    CONSTRAINT check_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE'))
);

-- Likes table
CREATE TABLE like (
    like_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    post_id UUID NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, post_id)
);

-- Comments table
CREATE TABLE comments (
    comment_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    parent_comment_id UUID REFERENCES comments(comment_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- COMMUNICATION FEATURES
-- ================================

-- Chat rooms table
CREATE TABLE chat_room (
    room_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_name VARCHAR(255),
    room_type VARCHAR(50) DEFAULT 'DIRECT',
    user1_id UUID REFERENCES users(user_id),
    user2_id UUID REFERENCES users(user_id),
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_by UUID REFERENCES users(user_id),
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_room_type CHECK (room_type IN ('DIRECT', 'GROUP', 'SUPPORT', 'LIVE_STREAM'))
);

-- Messages table
CREATE TABLE message (
    message_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    chat_room_id UUID NOT NULL REFERENCES chat_room(room_id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    message_type VARCHAR(50) DEFAULT 'TEXT',
    content TEXT,
    attachments JSONB,
    reply_to_message_id UUID REFERENCES message(message_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_message_type CHECK (message_type IN (
        'TEXT', 'IMAGE', 'VIDEO', 'FILE', 'VOICE', 'SYSTEM', 'ITEM_SHARE', 'LISTING_SHARE'
    ))
);

-- ================================
-- ADDITIONAL FEATURES
-- ================================

-- Live streaming sessions (enhanced)
CREATE TABLE live_stream_sessions (
    session_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    streamer_id UUID NOT NULL REFERENCES users(user_id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    stream_key VARCHAR(255),
    rtmp_url VARCHAR(500),
    playback_url VARCHAR(500),
    chat_room_id UUID REFERENCES chat_room(room_id),
    featured_items JSONB,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    viewer_count INTEGER DEFAULT 0,
    max_viewers INTEGER DEFAULT 0,
    total_views INTEGER DEFAULT 0,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    scheduled_start TIMESTAMP,
    actual_start TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    sales_during_stream DECIMAL(12,2) DEFAULT 0,
    is_public BOOLEAN DEFAULT TRUE,
    allow_comments BOOLEAN DEFAULT TRUE,
    allow_recording BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_stream_status CHECK (status IN ('SCHEDULED', 'LIVE', 'ENDED', 'CANCELLED'))
);

-- Loyalty points tracking (enhanced)
CREATE TABLE loyalty_points (
    point_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    transaction_type VARCHAR(50) NOT NULL,
    points INTEGER NOT NULL,
    description TEXT,
    source_activity VARCHAR(100),
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    balance_before INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    expires_at TIMESTAMP,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_loyalty_transaction_type CHECK (transaction_type IN (
        'EARNED_COLLECTION', 'EARNED_PURCHASE', 'EARNED_REVIEW', 'EARNED_REFERRAL',
        'EARNED_SOCIAL', 'SPENT_DISCOUNT', 'SPENT_PREMIUM', 'EXPIRED', 'ADJUSTMENT'
    )),
    CONSTRAINT check_loyalty_status CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'EXPIRED'))
);

-- Enhanced promotions
CREATE TABLE promotions (
    promotion_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    promotion_type VARCHAR(50) NOT NULL,
    discount_type VARCHAR(50),
    discount_value DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    max_uses INTEGER,
    max_uses_per_user INTEGER DEFAULT 1,
    current_uses INTEGER DEFAULT 0,
    eligible_user_types JSONB,
    eligible_categories JSONB,
    eligible_brands JSONB,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_promotion_type CHECK (promotion_type IN (
        'DISCOUNT', 'POINTS_MULTIPLIER', 'FREE_SHIPPING', 'GIFT', 'CASHBACK'
    )),
    CONSTRAINT check_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'POINTS') OR discount_type IS NULL)
);

-- ================================
-- INDEXES FOR PERFORMANCE
-- ================================

-- Role indexes
CREATE INDEX idx_role_name ON role(name);
CREATE INDEX idx_role_active ON role(is_active);

-- User roles indexes
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Social features indexes
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_posts_post_type ON posts(post_type);
CREATE INDEX idx_posts_visibility ON posts(visibility);

CREATE INDEX idx_likes_user_id ON like(user_id);
CREATE INDEX idx_likes_post_id ON like(post_id);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_comment_id);

-- Communication indexes
CREATE INDEX idx_chat_room_users ON chat_room(user1_id, user2_id);
CREATE INDEX idx_chat_room_item ON chat_room(item_id);
CREATE INDEX idx_chat_room_listing ON chat_room(listing_id);
CREATE INDEX idx_chat_room_active ON chat_room(is_active);

CREATE INDEX idx_message_chat_room ON message(chat_room_id);
CREATE INDEX idx_message_sender ON message(sender_id);
CREATE INDEX idx_message_created_at ON message(created_at);

-- Live stream indexes
CREATE INDEX idx_live_stream_streamer ON live_stream_sessions(streamer_id);
CREATE INDEX idx_live_stream_status ON live_stream_sessions(status);
CREATE INDEX idx_live_stream_scheduled ON live_stream_sessions(scheduled_start);

-- Loyalty points indexes
CREATE INDEX idx_loyalty_points_user ON loyalty_points(user_id);
CREATE INDEX idx_loyalty_points_type ON loyalty_points(transaction_type);
CREATE INDEX idx_loyalty_points_created ON loyalty_points(created_at);

-- GIN indexes for JSONB columns
CREATE INDEX idx_posts_hashtags_gin ON posts USING GIN(hashtags);
CREATE INDEX idx_loyalty_points_entity_gin ON loyalty_points USING GIN(related_entity_type);

-- ================================
-- INSERT DEFAULT ROLES
-- ================================

INSERT INTO role (name, description, permissions, is_system_role) VALUES
('SUPER_ADMIN', 'Super Administrator with full access', 'ALL', true),
('ADMIN', 'Administrator with management access', 'MANAGE_USERS,MANAGE_ITEMS,MANAGE_CATEGORIES,MANAGE_BRANDS,MANAGE_ORDERS,MANAGE_REPORTS', true),
('MODERATOR', 'Content moderator', 'MODERATE_CONTENT,MANAGE_REPORTS,MANAGE_REVIEWS', true),
('COLLECTOR', 'Collection service staff', 'MANAGE_COLLECTIONS,VALUATE_ITEMS,UPDATE_ITEM_STATUS', true),
('PREMIUM_USER', 'Premium user with extended features', 'CREATE_LISTINGS,LIVE_STREAM,EXTENDED_CHAT', true),
('USER', 'Regular user', 'CREATE_POSTS,COMMENT,LIKE,BASIC_CHAT,SUBMIT_ITEMS', true);

-- ================================
-- ASSIGN DEFAULT ROLES TO EXISTING USERS
-- ================================

-- This would typically be done by the application, but for migration:
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'USER' AND u.role = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'ADMIN' AND u.role = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'COLLECTOR' AND u.user_type = 'COLLECTOR';

COMMIT; 
-- Version 2.0 - Add missing tables from ERD

-- ================================
-- ROLE MANAGEMENT
-- ================================

CREATE TABLE role (
    role_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    permissions TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User roles junction table
CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES role(role_id) ON DELETE CASCADE,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by UUID REFERENCES users(user_id),
    PRIMARY KEY (user_id, role_id)
);

-- ================================
-- SOCIAL FEATURES
-- ================================

-- Posts table
CREATE TABLE posts (
    post_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT,
    images JSONB,
    videos JSONB,
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    post_type VARCHAR(50) DEFAULT 'GENERAL',
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    shares_count INTEGER DEFAULT 0,
    views_count INTEGER DEFAULT 0,
    hashtags JSONB,
    visibility VARCHAR(50) DEFAULT 'PUBLIC',
    is_featured BOOLEAN DEFAULT FALSE,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_post_type CHECK (post_type IN (
        'GENERAL', 'OUTFIT', 'STYLING_TIP', 'SUSTAINABILITY_TIP', 'ITEM_SHOWCASE',
        'BEFORE_AFTER', 'RECYCLING_STORY', 'BRAND_REVIEW', 'LIVE_STREAM_ANNOUNCEMENT'
    )),
    CONSTRAINT check_visibility CHECK (visibility IN ('PUBLIC', 'FOLLOWERS_ONLY', 'PRIVATE'))
);

-- Likes table
CREATE TABLE like (
    like_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    post_id UUID NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(user_id, post_id)
);

-- Comments table
CREATE TABLE comments (
    comment_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    post_id UUID NOT NULL REFERENCES posts(post_id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    parent_comment_id UUID REFERENCES comments(comment_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    is_hidden BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- COMMUNICATION FEATURES
-- ================================

-- Chat rooms table
CREATE TABLE chat_room (
    room_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_name VARCHAR(255),
    room_type VARCHAR(50) DEFAULT 'DIRECT',
    user1_id UUID REFERENCES users(user_id),
    user2_id UUID REFERENCES users(user_id),
    item_id UUID REFERENCES items(item_id),
    listing_id UUID REFERENCES marketplace_listings(listing_id),
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_by UUID REFERENCES users(user_id),
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_room_type CHECK (room_type IN ('DIRECT', 'GROUP', 'SUPPORT', 'LIVE_STREAM'))
);

-- Messages table
CREATE TABLE message (
    message_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    chat_room_id UUID NOT NULL REFERENCES chat_room(room_id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    message_type VARCHAR(50) DEFAULT 'TEXT',
    content TEXT,
    attachments JSONB,
    reply_to_message_id UUID REFERENCES message(message_id),
    is_edited BOOLEAN DEFAULT FALSE,
    edited_at TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_message_type CHECK (message_type IN (
        'TEXT', 'IMAGE', 'VIDEO', 'FILE', 'VOICE', 'SYSTEM', 'ITEM_SHARE', 'LISTING_SHARE'
    ))
);

-- ================================
-- ADDITIONAL FEATURES
-- ================================

-- Live streaming sessions (enhanced)
CREATE TABLE live_stream_sessions (
    session_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    streamer_id UUID NOT NULL REFERENCES users(user_id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    thumbnail_url VARCHAR(500),
    stream_key VARCHAR(255),
    rtmp_url VARCHAR(500),
    playback_url VARCHAR(500),
    chat_room_id UUID REFERENCES chat_room(room_id),
    featured_items JSONB,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    viewer_count INTEGER DEFAULT 0,
    max_viewers INTEGER DEFAULT 0,
    total_views INTEGER DEFAULT 0,
    likes_count INTEGER DEFAULT 0,
    comments_count INTEGER DEFAULT 0,
    scheduled_start TIMESTAMP,
    actual_start TIMESTAMP,
    ended_at TIMESTAMP,
    duration_seconds INTEGER,
    sales_during_stream DECIMAL(12,2) DEFAULT 0,
    is_public BOOLEAN DEFAULT TRUE,
    allow_comments BOOLEAN DEFAULT TRUE,
    allow_recording BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_stream_status CHECK (status IN ('SCHEDULED', 'LIVE', 'ENDED', 'CANCELLED'))
);

-- Loyalty points tracking (enhanced)
CREATE TABLE loyalty_points (
    point_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    transaction_type VARCHAR(50) NOT NULL,
    points INTEGER NOT NULL,
    description TEXT,
    source_activity VARCHAR(100),
    related_entity_type VARCHAR(50),
    related_entity_id UUID,
    balance_before INTEGER NOT NULL,
    balance_after INTEGER NOT NULL,
    expires_at TIMESTAMP,
    status VARCHAR(50) DEFAULT 'COMPLETED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_loyalty_transaction_type CHECK (transaction_type IN (
        'EARNED_COLLECTION', 'EARNED_PURCHASE', 'EARNED_REVIEW', 'EARNED_REFERRAL',
        'EARNED_SOCIAL', 'SPENT_DISCOUNT', 'SPENT_PREMIUM', 'EXPIRED', 'ADJUSTMENT'
    )),
    CONSTRAINT check_loyalty_status CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED', 'EXPIRED'))
);

-- Enhanced promotions
CREATE TABLE promotions (
    promotion_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    promotion_type VARCHAR(50) NOT NULL,
    discount_type VARCHAR(50),
    discount_value DECIMAL(10,2),
    max_discount_amount DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    max_uses INTEGER,
    max_uses_per_user INTEGER DEFAULT 1,
    current_uses INTEGER DEFAULT 0,
    eligible_user_types JSONB,
    eligible_categories JSONB,
    eligible_brands JSONB,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT check_promotion_type CHECK (promotion_type IN (
        'DISCOUNT', 'POINTS_MULTIPLIER', 'FREE_SHIPPING', 'GIFT', 'CASHBACK'
    )),
    CONSTRAINT check_discount_type CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT', 'POINTS') OR discount_type IS NULL)
);

-- ================================
-- INDEXES FOR PERFORMANCE
-- ================================

-- Role indexes
CREATE INDEX idx_role_name ON role(name);
CREATE INDEX idx_role_active ON role(is_active);

-- User roles indexes
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Social features indexes
CREATE INDEX idx_posts_user_id ON posts(user_id);
CREATE INDEX idx_posts_created_at ON posts(created_at);
CREATE INDEX idx_posts_post_type ON posts(post_type);
CREATE INDEX idx_posts_visibility ON posts(visibility);

CREATE INDEX idx_likes_user_id ON like(user_id);
CREATE INDEX idx_likes_post_id ON like(post_id);

CREATE INDEX idx_comments_post_id ON comments(post_id);
CREATE INDEX idx_comments_user_id ON comments(user_id);
CREATE INDEX idx_comments_parent_id ON comments(parent_comment_id);

-- Communication indexes
CREATE INDEX idx_chat_room_users ON chat_room(user1_id, user2_id);
CREATE INDEX idx_chat_room_item ON chat_room(item_id);
CREATE INDEX idx_chat_room_listing ON chat_room(listing_id);
CREATE INDEX idx_chat_room_active ON chat_room(is_active);

CREATE INDEX idx_message_chat_room ON message(chat_room_id);
CREATE INDEX idx_message_sender ON message(sender_id);
CREATE INDEX idx_message_created_at ON message(created_at);

-- Live stream indexes
CREATE INDEX idx_live_stream_streamer ON live_stream_sessions(streamer_id);
CREATE INDEX idx_live_stream_status ON live_stream_sessions(status);
CREATE INDEX idx_live_stream_scheduled ON live_stream_sessions(scheduled_start);

-- Loyalty points indexes
CREATE INDEX idx_loyalty_points_user ON loyalty_points(user_id);
CREATE INDEX idx_loyalty_points_type ON loyalty_points(transaction_type);
CREATE INDEX idx_loyalty_points_created ON loyalty_points(created_at);

-- GIN indexes for JSONB columns
CREATE INDEX idx_posts_hashtags_gin ON posts USING GIN(hashtags);
CREATE INDEX idx_loyalty_points_entity_gin ON loyalty_points USING GIN(related_entity_type);

-- ================================
-- INSERT DEFAULT ROLES
-- ================================

INSERT INTO role (name, description, permissions, is_system_role) VALUES
('SUPER_ADMIN', 'Super Administrator with full access', 'ALL', true),
('ADMIN', 'Administrator with management access', 'MANAGE_USERS,MANAGE_ITEMS,MANAGE_CATEGORIES,MANAGE_BRANDS,MANAGE_ORDERS,MANAGE_REPORTS', true),
('MODERATOR', 'Content moderator', 'MODERATE_CONTENT,MANAGE_REPORTS,MANAGE_REVIEWS', true),
('COLLECTOR', 'Collection service staff', 'MANAGE_COLLECTIONS,VALUATE_ITEMS,UPDATE_ITEM_STATUS', true),
('PREMIUM_USER', 'Premium user with extended features', 'CREATE_LISTINGS,LIVE_STREAM,EXTENDED_CHAT', true),
('USER', 'Regular user', 'CREATE_POSTS,COMMENT,LIKE,BASIC_CHAT,SUBMIT_ITEMS', true);

-- ================================
-- ASSIGN DEFAULT ROLES TO EXISTING USERS
-- ================================

-- This would typically be done by the application, but for migration:
INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'USER' AND u.role = 'USER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'ADMIN' AND u.role = 'ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u, role r
WHERE r.name = 'COLLECTOR' AND u.user_type = 'COLLECTOR';

COMMIT; 