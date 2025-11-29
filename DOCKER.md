# Bookthething Microservices - Docker Deployment

## Overview
This project consists of 6 microservices running in a coordinated Docker environment:

- **API Gateway** (Port 8080) - Routes requests to appropriate services
- **Auth Service** (Port 8081) - User authentication and JWT management  
- **User Service** (Port 8083) - User profile management
- **Metadata Service** (Port 8084) - Vendor and location management
- **Booking Orchestrator** (Port 8086) - Coordinates booking workflows
- **Futsal Service** (Port 8087) - Manages futsal court bookings
- **PostgreSQL Database** (Port 5432) - Shared data persistence

## Prerequisites

1. **Docker & Docker Compose** installed
2. **Minimum 4GB RAM** available for containers
3. **User permissions** for Docker:
   ```bash
   sudo usermod -aG docker $USER
   # Log out and back in to apply changes
   ```

## Quick Start

### Option 1: Automated Deployment
```bash
./docker-deploy.sh
```

### Option 2: Manual Deployment
```bash
# Clean up existing containers
docker-compose down --volumes

# Build and start all services
docker-compose up --build -d

# Check service status  
docker-compose ps

# View logs
docker-compose logs -f
```

## Service Architecture

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
       │
┌─────────────┐    ┌──────────────┐
│ Booking     │────│ Futsal       │
│ Orchestrator│    │ Service      │
│   :8086     │    │   :8087      │
└─────────────┘    └──────────────┘
       │                    │
       └────────────────────┴─── PostgreSQL :5432
```
       │                  │                    │
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
                             │                        │
                    ┌─────────────────────────────────┼──┐
                    │     PostgreSQL Database         │  │
                    │         :5432                   │  │
                    └─────────────────────────────────┼──┘
```

## API Endpoints

### Authentication
```bash
# Register new user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "email": "test@email.com", "password": "password123"}'

# Login user  
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'
```

### User Management  
```bash
# Create user profile (requires JWT token)
curl -X POST http://localhost:8080/api/v1/profile/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"username": "testuser", "fullName": "Test User", "email": "test@email.com"}'
```

### Booking Management
```bash
# Create booking (requires JWT token)
curl -X POST http://localhost:8080/api/v1/bookings/create \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"userId": 1, "timeSlot": "2025-12-01T10:00:00", "duration": 60, "serviceType": "futsal"}'
```

## Health Checks

All services expose health endpoints:
```bash
curl http://localhost:8080/actuator/health  # API Gateway
curl http://localhost:8081/actuator/health  # Auth Service
curl http://localhost:8083/actuator/health  # User Service
curl http://localhost:8086/actuator/health  # Booking Orchestrator  
curl http://localhost:8087/actuator/health  # Futsal Service
```

## Database Access

Connect to PostgreSQL:
```bash
# Using Docker
docker compose exec postgres psql -U jugger -d bookingdb

# Using local client
psql -h localhost -p 5432 -U jugger -d bookingdb
# Password: passw0rd
```

## Development

### View Service Logs
```bash
docker compose logs -f auth-service
docker compose logs -f user-service  
docker compose logs -f booking-orchestrator
docker compose logs -f futsal-service
docker compose logs -f api-gateway
```

### Rebuild Single Service
```bash
docker compose up --build -d auth-service
```

### Stop All Services
```bash
docker compose down --volumes
```

## Troubleshooting

### Service Won't Start
1. Check logs: `docker compose logs [service-name]`
2. Verify database connectivity: `docker compose exec postgres pg_isready`
3. Check port conflicts: `ss -tulpn | grep 80[8][0-9]`

### Database Issues
1. Reset database: `docker compose down --volumes && docker compose up postgres -d`
2. Check database connection: `docker compose exec postgres psql -U jugger -d bookingdb -c "\\l"`

### Network Issues
1. Check service network: `docker network ls`
2. Test inter-service connectivity:
   ```bash
   docker compose exec api-gateway curl http://auth-service:8081/actuator/health
   ```

## Project Structure

```
.
├── api-gateway/           # Spring Cloud Gateway
├── auth-service/          # JWT Authentication
├── userservice/           # User Profile Management
├── booking-orchestrator/  # Booking Workflow Coordination
├── futsal-service/        # Futsal Court Management
├── docker-compose.yml     # Container Orchestration
├── docker-deploy.sh       # Automated Deployment Script
└── DOCKER.md             # This documentation
```

## Integration Testing

The system has been tested for:
- ✅ Service compilation and startup
- ✅ Database connectivity and schema creation
- ✅ API Gateway routing to all services
- ✅ JWT token generation and validation
- ✅ Inter-service communication via RestTemplate
- ✅ Health monitoring and logging

Ready for production deployment!