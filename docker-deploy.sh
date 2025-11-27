#!/bin/bash

echo "=== Phase 4: Docker Integration Setup ==="
echo "This script will build and deploy all microservices using Docker Compose"
echo ""

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if user can access Docker
if ! docker info &> /dev/null; then
    echo "❌ Cannot access Docker. Please ensure:"
    echo "   1. Docker service is running: sudo systemctl start docker"
    echo "   2. User has Docker permissions: sudo usermod -aG docker $USER"
    echo "   3. Log out and log back in to apply group changes"
    echo ""
    echo "Alternative: Run with sudo: sudo ./docker-deploy.sh"
    exit 1
fi

echo "✅ Docker is accessible"

# Clean up any existing containers
echo ""
echo "🧹 Cleaning up existing containers and volumes..."
docker compose down --volumes --remove-orphans 2>/dev/null || true

# Build and start services
echo ""
echo "🏗️  Building and starting all microservices..."
echo "This may take several minutes as it downloads dependencies and builds each service..."

if docker compose up --build -d; then
    echo ""
    echo "✅ All services started successfully!"
    echo ""
    echo "🔍 Checking service health..."
    sleep 10
    
    echo ""
    echo "📊 Service Status:"
    echo "=================="
    docker compose ps
    
    echo ""
    echo "🌐 Available Endpoints:"
    echo "======================"
    echo "• API Gateway: http://localhost:8080"
    echo "• Auth Service: http://localhost:8081/actuator/health"
    echo "• User Service: http://localhost:8083/actuator/health"
    echo "• Booking Orchestrator: http://localhost:8086/actuator/health"
    echo "• Futsal Service: http://localhost:8087/actuator/health"
    echo "• PostgreSQL Database: localhost:5432 (bookingdb)"
    echo ""
    echo "🧪 Test Commands:"
    echo "=================="
    echo "# Test API Gateway health"
    echo "curl http://localhost:8080/actuator/health"
    echo ""
    echo "# Register new user through gateway"
    echo 'curl -X POST http://localhost:8080/api/v1/auth/register \'
    echo '  -H "Content-Type: application/json" \'
    echo '  -d {"username": "dockertest", "email": "docker@test.com", "password": "test123"}'
    echo ""
    echo "📋 View logs:"
    echo "============="
    echo "docker compose logs -f [service-name]"
    echo "docker compose logs -f auth-service"
    echo ""
    echo "🛑 Stop all services:"
    echo "===================="
    echo "docker compose down --volumes"
    
else
    echo ""
    echo "❌ Failed to start services. Check logs with:"
    echo "docker compose logs"
    exit 1
fi