# Bookthething - Microservices Application

A Spring Boot microservices application for booking and item management.

## Architecture

This project follows a microservices architecture with the following services:

### Services

1. **API Gateway** (Port: 8080)
   - Spring Cloud Gateway for routing requests
   - JWT authentication and authorization
   - CORS configuration
   - Service discovery and load balancing

2. **Booking Service** (Port: 8082)
   - Manages booking operations
   - PostgreSQL database integration
   - JPA/Hibernate for data persistence

## Technology Stack

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 21
- **Database**: PostgreSQL
- **API Gateway**: Spring Cloud Gateway
- **Security**: JWT (JSON Web Tokens)
- **Build Tool**: Maven
- **ORM**: JPA/Hibernate

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 12+
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Bookthething
```

### 2. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE bookingdb;
CREATE USER jugger WITH PASSWORD 'passw0rd';
GRANT ALL PRIVILEGES ON DATABASE bookingdb TO jugger;
```

### 3. Build All Services

```bash
# Build API Gateway
cd api-gateway
./mvnw clean install

# Build Booking Service
cd ../booking-service
./mvnw clean install
```

### 4. Run Services

Start services in the following order:

1. **Start API Gateway**:
   ```bash
   cd api-gateway
   ./mvnw spring-boot:run
   ```

2. **Start Booking Service**:
   ```bash
   cd booking-service
   ./mvnw spring-boot:run
   ```

## Service URLs

- **API Gateway**: http://localhost:8080
- **Booking Service**: http://localhost:8082

## API Documentation

### Authentication

All API calls (except public endpoints) require JWT authentication:

```
Authorization: Bearer <your-jwt-token>
```

### Available Endpoints

#### Through API Gateway

- **Booking Service**: `http://localhost:8080/api/bookings/**`
- **User Service**: `http://localhost:8080/api/users/**` (placeholder)
- **Item Service**: `http://localhost:8080/api/items/**` (placeholder)
- **Payment Service**: `http://localhost:8080/api/payments/**` (placeholder)

## Configuration

### Environment Variables

You can override default configurations using environment variables:

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/bookingdb
DB_USERNAME=jugger
DB_PASSWORD=passw0rd

# JWT Configuration
JWT_SECRET=your-256-bit-secret

# Server Ports
API_GATEWAY_PORT=8080
BOOKING_SERVICE_PORT=8082
```

### Application Properties

Each service has its own `application.yml` and `application.properties` files for configuration.

## Development

### Code Structure

```
Bookthething/
├── api-gateway/           # API Gateway service
│   ├── src/main/java/
│   │   └── com/jugger/bookthething/apigateway/
│   │       ├── config/    # Configuration classes
│   │       ├── filters/   # Gateway filters
│   │       └── utils/     # Utility classes
│   └── src/main/resources/
├── booking-service/       # Booking management service
│   ├── src/main/java/
│   │   └── com/jugger/bookingservice/
│   │       └── controller/ # REST controllers
│   └── src/main/resources/
└── README.md
```

### Building and Testing

```bash
# Run tests for all services
./mvnw test

# Run tests for specific service
cd api-gateway
./mvnw test

# Package applications
./mvnw clean package

# Skip tests during build
./mvnw clean package -DskipTests
```

## Docker Support

### Build Docker Images

```bash
# Build API Gateway
cd api-gateway
docker build -t bookthething/api-gateway:latest .

# Build Booking Service
cd ../booking-service
docker build -t bookthething/booking-service:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Security

- JWT tokens are used for authentication
- CORS is configured for frontend integration
- Sensitive information should be stored in environment variables

## Monitoring and Health

- Spring Boot Actuator endpoints are available for health monitoring
- Access health endpoint: `http://localhost:8080/actuator/health`

## Troubleshooting

### Common Issues

1. **Database Connection Issues**
   - Ensure PostgreSQL is running
   - Verify database credentials
   - Check network connectivity

2. **Service Discovery Issues**
   - Ensure all services are running
   - Check port conflicts
   - Verify gateway routing configuration

3. **JWT Authentication Issues**
   - Verify JWT secret configuration
   - Check token expiration
   - Ensure proper Authorization header format

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

- **Developer**: Jugger
- **Project**: Bookthething Microservices

---

## Roadmap

- [ ] User Service implementation
- [ ] Item Service implementation  
- [ ] Payment Service implementation
- [ ] Service discovery with Eureka
- [ ] Distributed tracing with Zipkin
- [ ] API documentation with Swagger/OpenAPI
- [ ] Containerization with Docker
- [ ] Kubernetes deployment
- [ ] CI/CD pipeline
- [ ] Monitoring with Prometheus/Grafana