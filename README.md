# GreenLoop Backend - Circular Fashion & Recycling Platform

GreenLoop is a comprehensive circular fashion and recycling platform that promotes sustainable fashion practices through buying, selling, renting, and recycling fashion items.

## Features

### 🌱 Core Features
- **User Authentication**: Secure registration, login with email verification and login alerts
- **Circular Marketplace**: Buy, sell, rent, and swap fashion items
- **Sustainability Tracking**: Monitor environmental impact and sustainability metrics
- **Item Management**: Comprehensive item cataloging with condition tracking
- **Category & Brand Management**: Organized fashion taxonomy

### 🔐 Security Features
- JWT-based authentication
- Email verification system
- Login notification alerts
- Password encryption with BCrypt
- Role-based access control

### 📊 Technical Features
- RESTful API with OpenAPI documentation
- PostgreSQL database with JSON support
- Email templates with Thymeleaf
- Comprehensive monitoring with Actuator
- Test coverage with JaCoCo

## Technology Stack

- **Framework**: Spring Boot 3.5.1
- **Language**: Java 21
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT
- **Email**: JavaMail + Thymeleaf templates
- **Documentation**: OpenAPI/Swagger
- **Build Tool**: Maven
- **Caching**: Redis
- **File Storage**: Cloudinary

## Getting Started

### Prerequisites
- Java 21 or higher
- PostgreSQL 12+
- Maven 3.8+ (or use the included wrapper)
- Redis (optional, for caching)

### Environment Variables
Create a `.env` file or set the following environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_NAME=greenloop
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Email Configuration
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# JWT Configuration
JWT_SECRET=your_jwt_secret_key_here

# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Frontend URL
FRONTEND_URL=http://localhost:3000

# Redis Configuration (optional)
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_USERNAME=
REDIS_PASSWORD=
```

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd green-loop-be
   ```

2. **Set up the database**
   ```sql
   CREATE DATABASE greenloop;
   ```

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8081`
   - Swagger UI: `http://localhost:8081/swagger-ui.html`
   - API Docs: `http://localhost:8081/v3/api-docs`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/verify?token={token}` - Verify email

### Protected Endpoints
All other endpoints require authentication via Bearer token in the Authorization header.

## Database Schema

### Core Entities
- **Users**: User accounts with authentication and profile info
- **Items**: Fashion items with sustainability tracking
- **Categories**: Hierarchical categorization system
- **Brands**: Fashion brand information
- **MarketplaceListings**: Buy/sell/rent listings
- **UserAddresses**: User location management
- **VerificationTokens**: Email verification system
- **SustainabilityMetrics**: Environmental impact tracking

## Architecture

The application follows a layered architecture:

```
├── controller/     # REST API endpoints
├── service/        # Business logic layer
├── repository/     # Data access layer
├── entity/         # JPA entities
├── security/       # Authentication & authorization
├── config/         # Configuration classes
├── enums/          # Enumeration types
└── utils/          # Utility classes
```

## Development

### Running Tests
```bash
./mvnw test
```

### Code Coverage
```bash
./mvnw jacoco:report
```

### Build for Production
```bash
./mvnw clean package -DskipTests
```

## Monitoring

The application includes Spring Boot Actuator endpoints:
- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus metrics: `/actuator/prometheus`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Contact

For questions or support, please contact the development team. 