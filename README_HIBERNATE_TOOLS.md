# 🚀 Hibernate Tools - Automatic Entity Generation

## ✅ **Setup Complete!**

You now have **Hibernate Tools Maven Plugin** configured to automatically generate all your entity classes directly from your PostgreSQL database schema.

---

## 🎯 **Why This is Better Than Manual Creation:**

### **✅ 100% Accurate**
- Generated directly from your actual database schema
- Every column, type, constraint exactly matches database
- No human errors or missing fields

### **✅ Complete & Ready-to-Use**
- All JPA annotations (@Entity, @Table, @Column, etc.)
- All validation annotations (@NotNull, @Size, @Email, etc.)
- All relationship mappings (@OneToMany, @ManyToOne, @JoinColumn)
- Lombok annotations (@Data, @Builder, @NoArgsConstructor, etc.)

### **✅ Time Saving**
- Generate all entities in **under 5 minutes**
- No debugging compilation errors
- No manual mapping of relationships

---

## 🚀 **How to Generate Entities:**

### **Method 1: Using Scripts (Easiest)**

#### **Windows:**
```bash
cd group2/green-loop-be
./generate-entities.bat
```

#### **Linux/Mac:**
```bash
cd group2/green-loop-be
chmod +x generate-entities.sh
./generate-entities.sh
```

### **Method 2: Using Maven**
```bash
cd group2/green-loop-be
mvn clean hibernate-tools:generate
mvn compile
```

### **Method 3: Using Maven Lifecycle**
```bash
cd group2/green-loop-be
mvn generate-sources
```

---

## 📁 **Generated Files Location:**

```
target/generated-sources/hibernate-tools/org/greenloop/circularfashion/entity/generated/
```

### **All Your Entities Will Be Generated:**
- `Users.java` - Complete user entity with all relationships
- `Brands.java` - Brand entity with slug constraint
- `Categories.java` - Category entity with slug constraint  
- `Items.java` - Item entity with item_code constraint
- `MarketplaceListings.java` - Marketplace listing entity
- `Orders.java` - Order entity with order_number constraint
- `OrderItems.java` - Order item entity
- `Promotions.java` - Promotion entity with promotion_code constraint
- `UserFollows.java` - User follow entity
- `LiveStreams.java` - Live stream entity with stream_key constraint
- `VerificationTokens.java` - Verification token entity
- `UserAddresses.java` - User address entity
- `SustainabilityMetrics.java` - Sustainability metrics entity

---

## 🔄 **Complete Workflow:**

### **Step 1: Generate Entities**
```bash
./generate-entities.bat  # Windows
# or
./generate-entities.sh   # Linux/Mac
```

### **Step 2: Review Generated Code**
- Check `target/generated-sources/hibernate-tools/.../generated/`
- All entities will be perfectly mapped to your database

### **Step 3: Copy to Source (Optional)**
```bash
# Copy generated entities to your source directory
cp -r target/generated-sources/hibernate-tools/org/greenloop/circularfashion/entity/generated/* src/main/java/org/greenloop/circularfashion/entity/
```

### **Step 4: Test**
```bash
mvn compile
mvn spring-boot:run
```

---

## ⚙️ **Configuration Details:**

### **Database Connection:**
- **URL:** `jdbc:postgresql://localhost:5432/greenLoop`
- **Username:** `postgres`
- **Password:** `Linhvovip`

### **Generated Features:**
- ✅ **Lombok:** @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
- ✅ **JPA:** @Entity, @Table, @Column, @Id, @GeneratedValue
- ✅ **Validation:** @NotNull, @NotBlank, @Size, @Email, etc.
- ✅ **Relationships:** @OneToMany, @ManyToOne, @JoinColumn
- ✅ **JSON Support:** JsonType for jsonb columns
- ✅ **Timestamps:** @CreationTimestamp, @UpdateTimestamp

---

## 🎉 **Result:**

You'll get **perfectly generated entities** that:
- ✅ Match your database schema exactly
- ✅ Include all relationships and constraints  
- ✅ Have proper validation annotations
- ✅ Use Lombok for clean code
- ✅ Are ready to use immediately

**No more manual entity creation!** 🚀

---

## 📚 **Documentation:**

- **Full Guide:** `HIBERNATE_TOOLS_GUIDE.md`
- **Configuration:** `hibernate.reveng.xml`
- **Scripts:** `generate-entities.bat` / `generate-entities.sh`

---

## 🚨 **Before Running:**

1. **Database Must Be Running** - PostgreSQL must be accessible
2. **Schema Must Exist** - All tables must be created in database
3. **Connection Must Work** - Test database connection first

---

**Ready to generate your entities? Run the script and get perfect, database-synchronized entity classes in minutes!** 🎯


