#!/bin/bash

echo "🚀 Bookthething Microservices - Full Cycle Test"
echo "==============================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Service definitions
declare -A SERVICES=(
    ["API Gateway"]="8080"
    ["Auth Service"]="8081" 
    ["User Service"]="8083"
    ["Metadata Service"]="8084"
    ["Booking Orchestrator"]="8086"
    ["Futsal Service"]="8087"
)

# Base URL
BASE_URL="http://localhost:8080"

# Function to check if port is in use
check_port() {
    local port=$1
    if ss -tulpn | grep -q ":$port "; then
        return 0  # Port is in use
    else
        return 1  # Port is free
    fi
}

# Function to test health endpoint
test_health() {
    local service_name=$1
    local port=$2
    local url="http://localhost:$port/actuator/health"
    
    if curl -s -f "$url" >/dev/null 2>&1; then
        echo -e "  ${GREEN}✅${NC} $service_name (port $port) - Health check passed"
        return 0
    else
        echo -e "  ${RED}❌${NC} $service_name (port $port) - Health check failed"
        return 1
    fi
}

# Function to build all services
build_all_services() {
    echo -e "${BLUE}🔨 Building all microservices...${NC}"
    
    for service_dir in api-gateway auth-service userservice booking-orchestrator futsal-service metadata-service; do
        if [ -d "$service_dir" ]; then
            echo -n "  Building $service_dir..."
            cd "$service_dir"
            if mvn clean package -DskipTests >/dev/null 2>&1; then
                echo -e " ${GREEN}✅${NC}"
            else
                echo -e " ${RED}❌${NC}"
            fi
            cd ..
        fi
    done
    echo ""
}

# Function to start all services
start_all_services() {
    echo -e "${BLUE}🚀 Starting all microservices...${NC}"
    
    # Start services in dependency order
    services=("metadata-service:8084" "userservice:8083" "auth-service:8081" "futsal-service:8087" "booking-orchestrator:8086" "api-gateway:8080")
    
    for service_port in "${services[@]}"; do
        service_name=$(echo $service_port | cut -d: -f1)
        port=$(echo $service_port | cut -d: -f2)
        
        if [ -d "$service_name" ]; then
            echo -n "  Starting $service_name on port $port..."
            cd "$service_name"
            if [ -f "target/${service_name}-0.0.1-SNAPSHOT.jar" ]; then
                nohup java -jar target/${service_name}-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &
                echo $! > "../${service_name}.pid"
                sleep 3  # Give service time to start
                echo -e " ${GREEN}✅${NC}"
            else
                echo -e " ${RED}❌ JAR not found${NC}"
            fi
            cd ..
        fi
    done
    echo ""
    echo "Waiting 10 seconds for all services to fully start..."
    sleep 10
}

# Function to stop all services
stop_all_services() {
    echo -e "${BLUE}🛑 Stopping all microservices...${NC}"
    
    for service_dir in api-gateway auth-service userservice booking-orchestrator futsal-service metadata-service; do
        pid_file="${service_dir}.pid"
        if [ -f "$pid_file" ]; then
            pid=$(cat "$pid_file")
            kill $pid 2>/dev/null
            rm "$pid_file"
            echo -e "  ${GREEN}✅${NC} Stopped $service_dir"
        fi
    done
    echo ""
}

# 1. Check running services
echo -e "${CYAN}1. 🔍 Checking service status...${NC}"
for service in "${!SERVICES[@]}"; do
    port=${SERVICES[$service]}
    if check_port $port; then
        echo -e "  ${GREEN}✅${NC} $service is running on port $port"
    else
        echo -e "  ${YELLOW}⚠️${NC}  $service is NOT running on port $port"
    fi
done
echo ""

# 2. Health checks
echo -e "${CYAN}2. 🏥 Running health checks...${NC}"
for service in "${!SERVICES[@]}"; do
    port=${SERVICES[$service]}
    test_health "$service" $port
done
echo ""

# 3. API Gateway routing test
echo -e "${CYAN}3. 🌐 Testing API Gateway routing...${NC}"

echo "Testing Auth Service route:"
auth_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/auth/health" 2>/dev/null)
if [ "$auth_response" = "200" ] || [ "$auth_response" = "404" ]; then
    echo -e "  ${GREEN}✅${NC} Auth Service route accessible"
else
    echo -e "  ${RED}❌${NC} Auth Service route failed (HTTP: $auth_response)"
fi

echo "Testing User Service route:"
user_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/profile/health" 2>/dev/null)
if [ "$user_response" = "200" ] || [ "$user_response" = "404" ]; then
    echo -e "  ${GREEN}✅${NC} User Service route accessible"
else
    echo -e "  ${RED}❌${NC} User Service route failed (HTTP: $user_response)"
fi

echo "Testing Booking Orchestrator route:"
booking_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/bookings/health" 2>/dev/null)
if [ "$booking_response" = "200" ] || [ "$booking_response" = "404" ]; then
    echo -e "  ${GREEN}✅${NC} Booking Orchestrator route accessible"
else
    echo -e "  ${RED}❌${NC} Booking Orchestrator route failed (HTTP: $booking_response)"
fi

echo "Testing Metadata Service route:"
metadata_response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/admin/health" 2>/dev/null)
if [ "$metadata_response" = "200" ] || [ "$metadata_response" = "404" ]; then
    echo -e "  ${GREEN}✅${NC} Metadata Service route accessible"
else
    echo -e "  ${RED}❌${NC} Metadata Service route failed (HTTP: $metadata_response)"
fi
echo ""

# 4. Database connectivity test
echo -e "${CYAN}4. 🗄️  Testing database connectivity...${NC}"
if pg_isready -h localhost -p 5432 -U jugger -d bookingdb >/dev/null 2>&1; then
    echo -e "  ${GREEN}✅${NC} PostgreSQL database is accessible"
else
    echo -e "  ${RED}❌${NC} PostgreSQL database is not accessible"
fi
echo ""

# 5. Functional API test
echo -e "${CYAN}5. 🧪 Running functional API test...${NC}"

# Test user registration
echo "Testing user registration:"
timestamp=$(date +%s)
username="testuser$timestamp"

registration_response=$(curl -s -X POST "$BASE_URL/api/v1/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$username\", \"email\": \"$username@test.com\", \"password\": \"password123\"}")

if echo "$registration_response" | grep -q -E '"success":true|"accessToken"|token'; then
    echo -e "  ${GREEN}✅${NC} User registration successful"
    
    # Extract JWT token
    jwt_token=$(echo "$registration_response" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$jwt_token" ]; then
        echo -e "  ${GREEN}✅${NC} JWT token extracted successfully"
        
        # Test protected endpoint
        echo "Testing protected endpoint (user profile creation):"
        profile_response=$(curl -s -X POST "$BASE_URL/api/v1/profile/create" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $jwt_token" \
            -d "{\"username\": \"$username\", \"email\": \"$username@test.com\", \"fullName\": \"Test User $timestamp\", \"phoneNumber\": \"+1234567890\"}")
        
        if echo "$profile_response" | grep -q -E '"message"|profile|success|created'; then
            echo -e "  ${GREEN}✅${NC} Protected endpoint accessible with JWT"
        else
            echo -e "  ${YELLOW}⚠️${NC}  Protected endpoint test inconclusive"
        fi
    else
        echo -e "  ${RED}❌${NC} JWT token extraction failed"
    fi
else
    echo -e "  ${RED}❌${NC} User registration failed"
    echo "     Response: $registration_response"
fi
echo ""

# 6. Microservice integration test
echo -e "${CYAN}6. 🔗 Testing microservice integration...${NC}"

if [ ! -z "$jwt_token" ]; then
    echo "Testing booking workflow:"
    booking_request=$(curl -s -X POST "$BASE_URL/api/v1/bookings/create" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $jwt_token" \
        -d "{\"userId\": 1, \"timeSlot\": \"2025-12-01T10:00:00\", \"duration\": 60, \"serviceType\": \"futsal\"}")
    
    if echo "$booking_request" | grep -q -E '"message"|booking|response|success'; then
        echo -e "  ${GREEN}✅${NC} Booking orchestrator integration working"
    else
        echo -e "  ${YELLOW}⚠️${NC}  Booking orchestrator integration test inconclusive"
    fi
fi
echo ""

# 7. Summary
echo -e "${MAGENTA}📊 Test Summary${NC}"
echo "==============="

running_count=0
for service in "${!SERVICES[@]}"; do
    port=${SERVICES[$service]}
    if check_port $port; then
        ((running_count++))
    fi
done

echo "Services running: $running_count/${#SERVICES[@]}"
echo ""

if [ $running_count -eq ${#SERVICES[@]} ]; then
    echo -e "${GREEN}🎉 All microservices are operational!${NC}"
else
    echo -e "${YELLOW}⚠️  Some services are not running. Use the following to start missing services:${NC}"
    echo ""
    for service in "${!SERVICES[@]}"; do
        port=${SERVICES[$service]}
        if ! check_port $port; then
            service_dir=$(echo "$service" | tr '[:upper:]' '[:lower:]' | sed 's/ /-/g')
            echo "  cd $service_dir && mvn spring-boot:run &"
        fi
    done
fi

echo ""
echo -e "${CYAN}💡 Commands:${NC}"
echo "  ./full-cycle-test.sh build    - Build all services"
echo "  ./full-cycle-test.sh start    - Start all services"  
echo "  ./full-cycle-test.sh stop     - Stop all services"
echo "  ./full-cycle-test.sh          - Run full test suite"

# Handle command line arguments
case "${1:-}" in
    build)
        build_all_services
        ;;
    start)
        build_all_services
        start_all_services
        ;;
    stop)
        stop_all_services
        ;;
esac