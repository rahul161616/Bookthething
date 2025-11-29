#!/bin/bash

# Comprehensive Booking Flow Test Script
# Tests the complete booking workflow through API Gateway

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Base URLs
API_GATEWAY="http://localhost:8080"
AUTH_SERVICE="http://localhost:8081"
USER_SERVICE="http://localhost:8084"
VENDOR_SERVICE="http://localhost:8083"
METADATA_SERVICE="http://localhost:8086"
FUTSAL_SERVICE="http://localhost:8087"
ORCHESTRATOR="http://localhost:8085"

# Test variables
JWT_TOKEN=""
USER_ID=""
VENDOR_ID=""
BOOKING_ID=""

# Function to check service health
check_service_health() {
    local service_name=$1
    local url=$2
    
    print_info "Checking $service_name health..."
    if curl -s "$url/actuator/health" | grep -q "UP"; then
        print_success "$service_name is healthy"
        return 0
    else
        print_error "$service_name is not healthy"
        return 1
    fi
}

# Function to register a test user
register_user() {
    print_info "Registering test user..."
    
    local response=$(curl -s -X POST "$AUTH_SERVICE/api/auth/register" \
        -H "Content-Type: application/json" \
        -d '{
            "email": "testuser@example.com",
            "password": "TestPassword123!",
            "fullName": "Test User",
            "phoneNumber": "1234567890"
        }')
    
    if echo "$response" | grep -q "User registered successfully"; then
        print_success "User registered successfully"
        return 0
    else
        print_warning "User might already exist or registration failed: $response"
        return 0  # Continue anyway
    fi
}

# Function to login and get JWT token
login_user() {
    print_info "Logging in user..."
    
    local response=$(curl -s -X POST "$AUTH_SERVICE/api/auth/login" \
        -H "Content-Type: application/json" \
        -d '{
            "email": "testuser@example.com",
            "password": "TestPassword123!"
        }')
    
    JWT_TOKEN=$(echo "$response" | jq -r '.token // empty')
    USER_ID=$(echo "$response" | jq -r '.userId // empty')
    
    if [[ -n "$JWT_TOKEN" && "$JWT_TOKEN" != "null" ]]; then
        print_success "Login successful, JWT token obtained"
        print_info "User ID: $USER_ID"
        return 0
    else
        print_error "Login failed: $response"
        return 1
    fi
}

# Function to create a test vendor
create_vendor() {
    print_info "Creating test vendor..."
    
    local response=$(curl -s -X POST "$VENDOR_SERVICE/api/vendors" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d '{
            "name": "Test Futsal Arena",
            "location": "Test City",
            "contactInfo": "test@futsalarena.com",
            "description": "Premium futsal facility for testing"
        }')
    
    VENDOR_ID=$(echo "$response" | jq -r '.id // empty')
    
    if [[ -n "$VENDOR_ID" && "$VENDOR_ID" != "null" ]]; then
        print_success "Vendor created successfully"
        print_info "Vendor ID: $VENDOR_ID"
        return 0
    else
        print_error "Vendor creation failed: $response"
        return 1
    fi
}

# Function to test booking creation
create_booking() {
    print_info "Creating futsal booking..."
    
    # Calculate future dates
    local start_time=$(date -d "+2 hours" -Iseconds)
    local end_time=$(date -d "+3 hours" -Iseconds)
    
    local response=$(curl -s -X POST "$API_GATEWAY/api/bookings" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d "{
            \"userId\": $USER_ID,
            \"vendorId\": $VENDOR_ID,
            \"bookingType\": \"futsal\",
            \"dateTime\": \"$start_time\",
            \"durationMinutes\": 60,
            \"participants\": 10,
            \"price\": 50.0,
            \"extraInfo\": \"Test booking via automation\"
        }")
    
    BOOKING_ID=$(echo "$response" | jq -r '.bookingId // empty')
    local status=$(echo "$response" | jq -r '.status // empty')
    
    if [[ -n "$BOOKING_ID" && "$BOOKING_ID" != "null" && "$status" == "PENDING" ]]; then
        print_success "Booking created successfully"
        print_info "Booking ID: $BOOKING_ID"
        print_info "Status: $status"
        return 0
    else
        print_error "Booking creation failed: $response"
        return 1
    fi
}

# Function to test booking status update
update_booking_status() {
    print_info "Updating booking status to CONFIRMED..."
    
    local response=$(curl -s -X POST "$FUTSAL_SERVICE/api/v1/futsal/$BOOKING_ID/status" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -d '{
            "status": "CONFIRMED"
        }')
    
    local status=$(echo "$response" | jq -r '.status // empty')
    
    if [[ "$status" == "CONFIRMED" ]]; then
        print_success "Booking status updated to CONFIRMED"
        return 0
    else
        print_error "Booking status update failed: $response"
        return 1
    fi
}

# Function to run all tests
run_comprehensive_test() {
    echo "========================================"
    echo "  🧪 Bookthething Booking Flow Test"
    echo "========================================"
    echo ""
    
    print_info "Starting comprehensive booking flow test..."
    
    # Check all service health
    print_info "Checking all services health..."
    check_service_health "Auth Service" "$AUTH_SERVICE" || exit 1
    check_service_health "User Service" "$USER_SERVICE" || exit 1
    check_service_health "Vendor Service" "$VENDOR_SERVICE" || exit 1
    check_service_health "Metadata Service" "$METADATA_SERVICE" || exit 1
    check_service_health "Futsal Service" "$FUTSAL_SERVICE" || exit 1
    check_service_health "Booking Orchestrator" "$ORCHESTRATOR" || exit 1
    
    echo ""
    print_success "All services are healthy!"
    echo ""
    
    # Run test flow
    register_user || exit 1
    sleep 2
    
    login_user || exit 1
    sleep 2
    
    create_vendor || exit 1
    sleep 2
    
    create_booking || exit 1
    sleep 2
    
    update_booking_status || exit 1
    
    echo ""
    echo "========================================"
    print_success "🎉 All tests completed successfully!"
    echo "========================================"
    echo ""
    echo "Test Summary:"
    echo "- User registered and logged in ✅"
    echo "- Vendor created ✅"
    echo "- Booking created via API Gateway ✅"
    echo "- Booking status updated ✅"
    echo ""
    echo "Test Data Created:"
    echo "- User ID: $USER_ID"
    echo "- Vendor ID: $VENDOR_ID"
    echo "- Booking ID: $BOOKING_ID"
    echo ""
}

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    print_error "jq is required but not installed. Please install jq first."
    echo "On Ubuntu/Debian: sudo apt install jq"
    echo "On macOS: brew install jq"
    exit 1
fi

# Run the comprehensive test
run_comprehensive_test

echo "✨ Booking flow test completed!"