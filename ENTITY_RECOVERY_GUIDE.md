# Entity Recovery Guide

## Database Schema Analysis
Based on the unique constraints provided, here are the entities that exist in your database:

### Core Entities Found:
1. **brands** - with unique `slug`
2. **categories** - with unique `slug` 
3. **items** - with unique `item_code`
4. **live_streams** - with unique `stream_key`
5. **orders** - with unique `order_number`
6. **promotions** - with unique `promotion_code`
7. **user_follows** - with unique `followed_id` and `follower_id`
8. **users** - with unique `email` and `username`
9. **verification_tokens** - with unique `token` and `user_id`

## Missing Entities to Create:
Based on the schema, I'll create the missing entity classes for you.

## Instructions:
1. Run the backend application
2. Visit `http://localhost:8080/api/admin/export-schema` to get full database schema
3. Use the created entity classes below
4. Update any missing relationships

## Next Steps:
1. Start the backend: `./mvnw spring-boot:run`
2. Check console logs for schema export
3. Review and adjust entity classes as needed
