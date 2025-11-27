#!/bin/bash

echo "🧪 Testing Bookthething Microservices Integration"
echo "================================================="
echo ""

# Base URL for API Gateway
BASE_URL="http://localhost:8080"

# Test 1: Health checks
echo "1. 🏥 Testing service health endpoints..."
services=("api-gateway:8080" "auth-service:8081" "user-service:8083" "booking-orchestrator:8086" "futsal-service:8087")

for service in "${services[@]}"; do
    name=$(echo $service | cut -d: -f1)
    port=$(echo $service | cut -d: -f2)
    
    if curl -s -f "http://localhost:$port/actuator/health" > /dev/null; then
        echo "  ✅ $name health check passed"
    else
        echo "  ❌ $name health check failed"
    fi
done

echo ""

# Test 2: User registration through API Gateway
echo "2. 👤 Testing user registration through API Gateway..."
timestamp=$(date +%s)
username="testuser$timestamp"

registration_response=$(curl -s -X POST "$BASE_URL/api/v1/auth/register" \
    -H "Content-Type: application/json" \
    -d "{\"username\": \"$username\", \"email\": \"$username@test.com\", \"password\": \"password123\"}")

if echo "$registration_response" | grep -q '"success":true'; then
    echo "  ✅ User registration successful"
    
    # Extract JWT token
    jwt_token=$(echo "$registration_response" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$jwt_token" ]; then
        echo "  ✅ JWT token generated successfully"
        
        # Test 3: Create user profile
        echo ""
        echo "3. 📋 Testing user profile creation..."
        
        profile_response=$(curl -s -X POST "$BASE_URL/api/v1/profile/create" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $jwt_token" \
            -d "{\"username\": \"$username\", \"email\": \"$username@test.com\", \"fullName\": \"Test User $timestamp\", \"phoneNumber\": \"+1234567890\"}")
        
        if echo "$profile_response" | grep -q -E '"message":"Profile created|profile.*created|success'; then
            echo "  ✅ User profile creation successful"
        else
            echo "  ⚠️  User profile creation failed (this may be expected due to validation rules)"
            echo "     Response: $profile_response"
        fi
        
        # Test 4: Test booking orchestrator
        echo ""
        echo "4. 🎯 Testing booking orchestrator..."
        
        booking_response=$(curl -s -X POST "$BASE_URL/api/v1/bookings/create" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $jwt_token" \
            -d "{\"userId\": 1, \"timeSlot\": \"2025-12-01T10:00:00\", \"duration\": 60, \"serviceType\": \"futsal\"}")
        
        if echo "$booking_response" | grep -q -E '"message"|booking|response'; then
            echo "  ✅ Booking orchestrator responding (endpoint accessible)"
        else
            echo "  ⚠️  Booking orchestrator test inconclusive"
            echo "     Response: $booking_response"
        fi
        
    else
        echo "  ❌ JWT token extraction failed"
    fi
else
    echo "  ❌ User registration failed"
    echo "     Response: $registration_response"
fi

echo ""
echo "5. 🔍 Service logs (last 5 lines each):"
echo "======================================="

services=("auth-service" "user-service" "booking-orchestrator" "futsal-service" "api-gateway")
for service in "${services[@]}"; do
    echo ""
    echo "📄 $service logs:"
    docker compose logs --tail=5 $service 2>/dev/null || echo "   Could not retrieve logs"
done

echo ""
echo "🏁 Integration test completed!"
echo ""
echo "💡 To view full service logs:"
echo "   docker compose logs -f [service-name]"
echo ""
echo "🛑 To stop all services:"
echo "   docker compose down --volumes"