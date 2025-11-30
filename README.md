# BookTheThing - Enterprise Microservices Booking Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED.svg)](https://www.docker.com/)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](/)

A production-ready, enterprise-grade microservices platform for multi-tenant booking management with real-time availability, automated workflows, and comprehensive vendor management.

## 🏗️ Architecture Overview

BookTheThing follows a **distributed microservices architecture** with **domain-driven design principles**, featuring 6 independent services orchestrated through an API Gateway pattern.

```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│ API Gateway │────│ Auth Service │    │User Service │
│   :8080     │    │    :8081     │    │   :8083     │
└─────────────┘    └──────────────┘    └─────────────┘
       │                                      │
       │            ┌──────────────┐          │
       └────────────│  Metadata    │──────────┘
                    │  Service     │
                    │   :8084      │
                    └──────────────┘
       │                    │
       │                    │
       └────────────────────┴─── PostgreSQL :5432
       │                  │                    
       │                  └────────────────────┼──────┐
       │                                       │      │
       │            ┌──────────────────┐       │      │
       └────────────│Booking Orchestr. │───────┘      │
                    │      :8086       │              │
                    └──────────────────┘              │
                             │                        │
                    ┌─────────────────┐               │
                    │ Futsal Service  │               │
                    │     :8087       │               │
                    └─────────────────┘               │
```

## 🚀 Core Services

### 1. **API Gateway** (Port: 8080)
- **Spring Cloud Gateway** for intelligent request routing
- **JWT Authentication** with role-based access control (USER/VENDOR/ADMIN)
- **CORS Configuration** for frontend integration
- **Service Discovery** and automatic load balancing
- **Rate Limiting** and security filtering

### 2. **Auth Service** (Port: 8081)
- **JWT Token Management** with refresh token support
- **User Registration** and authentication
- **Role-Based Authorization** (USER/VENDOR/ADMIN)
- **Password Security** with BCrypt encryption
- **Session Management** and token validation

### 3. **User Service** (Port: 8083)
- **User Profile Management** with comprehensive data handling
- **Enhanced Vendor Metadata** with scheduling capabilities
- **Service Registration** for vendor onboarding
- **Profile Validation** and data integrity

### 4. **Metadata Service** (Port: 8084)
- **Booking Type Management** (Futsal, Rooms, Events)
- **Vendor Application Processing** with approval workflows
- **Admin Functions** for platform management
- **Service Catalog** management

### 5. **Booking Orchestrator** (Port: 8086)
- **Business Process Orchestration** across multiple services
- **Complex Workflow Management** with error handling
- **Service Coordination** and transaction management
- **Business Rule Engine** for booking validation

### 6. **Futsal Service** (Port: 8087)
- **Real-Time Availability Engine** with dynamic scheduling
- **Booking Management** with status transitions
- **Vendor-Specific Configuration** support
- **Slot Management** with configurable durations

## 💡 Key Features

### 🔐 **Enterprise Security**
- **JWT-Based Authentication** with automatic token propagation
- **Role-Based Access Control** with fine-grained permissions
- **API Gateway Security** with request validation
- **Service-to-Service Authentication** with header injection

### 📅 **Dynamic Availability System**
- **Real-Time Scheduling** with vendor-configurable hours
- **Blocked Dates** management for holidays/maintenance
- **Special Event Hours** with custom scheduling
- **Slot Duration Configuration** (90-minute default, customizable)
- **Conflict Prevention** with double-booking protection

### 🔄 **Workflow Automation**
- **Complete Booking Lifecycle**: Registration → Application → Approval → Service Setup → Booking → Confirmation
- **Automated Status Transitions**: PENDING → APPROVED → REJECTED → CANCELLED
- **Vendor Application Process** with admin approval workflow
- **Multi-Service Orchestration** with proper error handling

### 📊 **Data Management**
- **PostgreSQL Database** with proper service isolation
- **Normalized Schema Design** with foreign key relationships
- **Enum-Based Status Management** with database constraints
- **Audit Trails** and comprehensive logging

## 🛠️ Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Framework** | Spring Boot | 4.0.0 |
| **Language** | Java | 21 |
| **Database** | PostgreSQL | 17 |
| **Gateway** | Spring Cloud Gateway | Latest |
| **Security** | Spring Security + JWT | Latest |
| **Build Tool** | Maven | 3.9+ |
| **ORM** | JPA/Hibernate | Latest |
| **Containerization** | Docker + Docker Compose | Latest |

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.9+** 
- **PostgreSQL 17**
- **Docker** (optional)
- **Git**

### 1. Clone Repository

```bash
git clone https://github.com/rahul161616/Bookthething.git
cd Bookthething
```

### 2. Database Setup

```sql
CREATE DATABASE bookingdb;
CREATE USER jugger WITH PASSWORD 'passw0rd';
GRANT ALL PRIVILEGES ON DATABASE bookingdb TO jugger;
```

### 3. Quick Setup (Automated)

```bash
# Make setup script executable and run
chmod +x setup.sh
./setup.sh

# Start all services
./start-services.sh
```

### 4. Manual Setup

```bash
# Build all services
./full-cycle-test.sh build

# Start services individually
cd auth-service && ./mvnw spring-boot:run &
cd userservice && ./mvnw spring-boot:run &
cd metadata-service && ./mvnw spring-boot:run &
cd futsal-service && ./mvnw spring-boot:run &
cd booking-orchestrator && ./mvnw spring-boot:run &
cd api-gateway && ./mvnw spring-boot:run &
```

### 5. Docker Deployment (Recommended)

```bash
# Build and start all services with Docker
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## 📍 Service URLs

| Service | URL | Health Check |
|---------|-----|--------------|
| **API Gateway** | http://localhost:8080 | /actuator/health |
| **Auth Service** | http://localhost:8081 | /actuator/health |
| **User Service** | http://localhost:8083 | /actuator/health |
| **Metadata Service** | http://localhost:8084 | /actuator/health |
| **Booking Orchestrator** | http://localhost:8086 | /actuator/health |
| **Futsal Service** | http://localhost:8087 | /api/v1/futsal/health |

## 🧪 Testing

### Automated Testing

```bash
# Run comprehensive integration tests
./test-integration.sh

# Test complete booking workflow
./test-booking-flow.sh

# Full cycle testing (build + test + start)
./full-cycle-test.sh
```

### Manual API Testing

Comprehensive API documentation with test examples is available in:
- **[POSTMANMANUALTESTENDPOINTS.md](./POSTMANMANUALTESTENDPOINTS.md)** - Complete API reference
- **[FUTSAL_BOOKING_WORKFLOW_TEST.md](./FUTSAL_BOOKING_WORKFLOW_TEST.md)** - Workflow test results

### Example API Calls

```bash
# User Registration
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123", "role": "USER"}'

# Check Futsal Availability
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/futsal/availability?vendorId=2&date=2025-12-02"

# Create Booking
curl -X POST http://localhost:8080/api/v1/futsal/book \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"vendorId": 2, "startTime": "2025-12-02T09:00:00+00:00", ...}'
```

## 📁 Project Structure

```
BookTheThing/
├── api-gateway/              # Spring Cloud Gateway (Port 8080)
├── auth-service/             # JWT Authentication (Port 8081)
├── userservice/              # User & Vendor Management (Port 8083)
├── metadata-service/         # Admin & Booking Types (Port 8084)
├── booking-orchestrator/     # Business Logic Orchestration (Port 8086)
├── futsal-service/           # Futsal Booking Service (Port 8087)
├── vendor-service/           # Legacy Vendor Service (Deprecated)
├── docker-compose.yml        # Container orchestration
├── setup.sh                  # Automated setup script
├── *.sh                      # Testing and utility scripts
├── *.md                      # Comprehensive documentation
└── README.md                 # This file
```

## 🔧 Configuration

### Environment Variables

```bash
# Database Configuration
DB_URL=jdbc:postgresql://localhost:5432/bookingdb
DB_USERNAME=jugger
DB_PASSWORD=passw0rd

# JWT Configuration  
JWT_SECRET=mySecretKey
JWT_EXPIRATION=3600000

# Service Ports
API_GATEWAY_PORT=8080
AUTH_SERVICE_PORT=8081
USER_SERVICE_PORT=8083
METADATA_SERVICE_PORT=8084
ORCHESTRATOR_PORT=8086
FUTSAL_SERVICE_PORT=8087
```

## 📈 Monitoring & Observability

- **Health Endpoints**: All services expose `/actuator/health`
- **Application Logs**: Comprehensive logging with service-specific log files
- **Performance Metrics**: Spring Boot Actuator integration
- **Service Status**: Real-time health monitoring dashboard

## 🔒 Security Features

- **JWT Authentication** with role-based authorization
- **CORS Protection** with configurable origins
- **Input Validation** across all service endpoints
- **SQL Injection Prevention** through JPA/Hibernate
- **Password Encryption** using BCrypt
- **API Rate Limiting** at gateway level

## 📚 Documentation

| Document | Description |
|----------|-------------|
| **[MICROSERVICES_ARCHITECTURE_WORKFLOW.md](./MICROSERVICES_ARCHITECTURE_WORKFLOW.md)** | Detailed architecture patterns |
| **[DOCKER.md](./DOCKER.md)** | Container deployment guide |
| **[DATABASE_FIXES_SUMMARY.md](./DATABASE_FIXES_SUMMARY.md)** | Database schema details |
| **[SECURITY.md](./SECURITY.md)** | Security implementation guide |
| **[CONTRIBUTING.md](./CONTRIBUTING.md)** | Development contribution guide |

## 🎯 Production Readiness

✅ **Complete End-to-End Testing** (100% workflow success)  
✅ **Container Orchestration** with Docker Compose  
✅ **Health Monitoring** across all services  
✅ **Error Handling** with graceful degradation  
✅ **Security Implementation** with JWT and RBAC  
✅ **Database Optimization** with proper indexing  
✅ **Service Isolation** with independent deployments  
✅ **Comprehensive Documentation** for operations  

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📞 Support

For questions, issues, or contributions:

- **GitHub Issues**: [Create an issue](https://github.com/rahul161616/Bookthething/issues)
- **Documentation**: See `/docs` folder for detailed guides
- **Email**: Available upon request

---

**BookTheThing** - *Enterprise Microservices. Production Ready. Scalable.*