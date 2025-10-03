# Hibernate Tools - Automatic Entity Generation Guide

## 🚀 **Why Use Hibernate Tools?**

### **Advantages over Manual Creation:**
- ✅ **100% Accurate** - Generated directly from database schema
- ✅ **No Human Errors** - Automatic mapping of all columns, types, constraints
- ✅ **Complete Relationships** - Foreign keys, joins, cascades automatically mapped
- ✅ **Validation Annotations** - Automatic generation of validation rules
- ✅ **Lombok Integration** - Automatic @Data, @Builder, @NoArgsConstructor, etc.
- ✅ **JPA Annotations** - Complete @Entity, @Table, @Column mappings
- ✅ **Time Saving** - Generate all entities in seconds vs hours of manual work

---

## 🛠️ **Setup Complete**

### **Added to pom.xml:**
- Hibernate Tools Maven Plugin (6.4.0.Final)
- Automatic entity generation on `mvn generate-sources`
- Lombok integration for clean code
- JPA/EJB3 annotations
- Validation annotations

### **Configuration Files:**
- `hibernate.reveng.xml` - Reverse engineering configuration
- `generate-entities.bat` - Windows script
- `generate-entities.sh` - Linux/Mac script

---

## 🚀 **How to Generate Entities**

### **Method 1: Using Scripts (Recommended)**

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

### **Method 2: Using Maven Directly**
```bash
cd group2/green-loop-be

# Clean and generate
mvn clean hibernate-tools:generate

# Compile generated entities
mvn compile
```

### **Method 3: Using Maven Lifecycle**
```bash
cd group2/green-loop-be
mvn generate-sources
```

---

## 📁 **Generated Files Location**

### **Output Directory:**
```
target/generated-sources/hibernate-tools/org/greenloop/circularfashion/entity/generated/
```

### **Generated Entities:**
- `Users.java` - User entity with all fields and relationships
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

## 🔄 **Workflow**

### **Step 1: Generate Entities**
```bash
./generate-entities.bat  # or .sh
```

### **Step 2: Review Generated Code**
- Check `target/generated-sources/hibernate-tools/.../generated/`
- Review entity mappings and relationships
- Verify all constraints are properly mapped

### **Step 3: Copy to Source**
```bash
# Copy generated entities to source directory
cp -r target/generated-sources/hibernate-tools/org/greenloop/circularfashion/entity/generated/* src/main/java/org/greenloop/circularfashion/entity/
```

### **Step 4: Customize (Optional)**
- Add custom methods
- Modify annotations if needed
- Add business logic

### **Step 5: Test**
```bash
mvn compile
mvn spring-boot:run
```

---

## ⚙️ **Configuration Details**

### **Database Connection:**
- **URL:** `jdbc:postgresql://localhost:5432/greenLoop`
- **Username:** `postgres`
- **Password:** `Linhvovip`
- **Driver:** `org.postgresql.Driver`

### **Generated Features:**
- ✅ **Lombok Annotations:** @Data, @Builder, @NoArgsConstructor, @AllArgsConstructor
- ✅ **JPA Annotations:** @Entity, @Table, @Column, @Id, @GeneratedValue
- ✅ **Validation Annotations:** @NotNull, @NotBlank, @Size, @Email, etc.
- ✅ **Relationship Mappings:** @OneToMany, @ManyToOne, @JoinColumn
- ✅ **JSON Support:** JsonType for jsonb columns
- ✅ **Timestamps:** @CreationTimestamp, @UpdateTimestamp

---

## 🎯 **Benefits Over Manual Creation**

### **Accuracy:**
- **100% Schema Match** - Every column, type, constraint exactly matches database
- **No Missing Fields** - All database columns automatically included
- **Correct Data Types** - PostgreSQL types properly mapped to Java types
- **Proper Constraints** - Unique, foreign key, check constraints all mapped

### **Relationships:**
- **Foreign Keys** - Automatically mapped to @ManyToOne/@OneToMany
- **Join Columns** - @JoinColumn annotations with correct column names
- **Cascade Types** - Appropriate cascade settings based on constraints
- **Fetch Types** - Optimized LAZY/EAGER loading

### **Time Saving:**
- **Minutes vs Hours** - Generate all entities in under 5 minutes
- **No Debugging** - No compilation errors from manual mistakes
- **Consistent Style** - All entities follow same patterns and conventions

---

## 🔧 **Customization Options**

### **Modify hibernate.reveng.xml:**
- Add custom column mappings
- Exclude specific tables
- Customize package names
- Add custom type mappings

### **Post-Generation:**
- Add custom methods to entities
- Modify annotations if needed
- Add business logic
- Create custom constructors

---

## 🚨 **Important Notes**

### **Before Generation:**
1. **Database Must Be Running** - PostgreSQL must be accessible
2. **Schema Must Exist** - All tables must be created in database
3. **Connection Must Work** - Test database connection first

### **After Generation:**
1. **Review Generated Code** - Check for any issues
2. **Test Compilation** - Ensure all entities compile
3. **Test Application** - Verify application starts successfully

---

## 🎉 **Result**

You'll get **perfectly generated entities** that:
- ✅ Match your database schema exactly
- ✅ Include all relationships and constraints
- ✅ Have proper validation annotations
- ✅ Use Lombok for clean code
- ✅ Are ready to use immediately

**No more manual entity creation!** 🚀
