# Postman Manual Test Endpoints - Bookthething

**Base URLs:**
- API Gateway: `http://localhost:8080`
- Auth Service: `http://localhost:8081`  
- User Service: `http://localhost:8083`
- Metadata Service: `http://localhost:8084`
- Booking Orchestrator: `http://localhost:8086`
- Futsal Service: `http://localhost:8087`

**Database Schema Reference:**
- All IDs are `BIGINT` (Long) except futsal service which was converted from UUID
- User table uses `username`, `password`, `role`, `isVendor`, `isAdmin`
- Foreign keys: `userId`, `vendorId`, `bookingTypeId`

---

## 🔐 1. User Registration & Authentication Flow

### 1.1 User Registration
```http
POST {{auth_base}}/api/v1/auth/register
Content-Type: application/json

{
  "username": "testuser1",
  "password": "password123",
  "email": "user1@test.com",
  "role": "USER"
}
```

### 1.2 Vendor Registration  
```http
POST {{auth_base}}/api/v1/auth/register
Content-Type: application/json

{
  "username": "vendor1",
  "password": "password123", 
  "email": "vendor1@test.com",
  "role": "VENDOR"
}
```

### 1.3 Admin Registration
```http
POST {{auth_base}}/api/v1/auth/register
Content-Type: application/json

{
  "username": "admin1",
  "password": "adminpass123",
  "email": "admin@test.com", 
  "role": "ADMIN"
}
```

### 1.4 User Login
```http
POST {{auth_base}}/api/v1/auth/login
Content-Type: application/json

{
  "username": "testuser1",
  "password": "password123"
}
```

**Expected Response:**
```json
{
  "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "refresh_token_string",
  "message": "Login successful", 
  "success": true
}
```

### 1.5 Admin Login
```http
POST {{auth_base}}/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin1",
  "password": "adminpass123"
}
```

### 1.6 Vendor Login
```http
POST {{auth_base}}/api/v1/auth/login 
Content-Type: application/json

{
  "username": "vendor1",
  "password": "password123"
}
```

---

## 👤 2. User Profile Creation

### 2.1 Create User Profile
```http
POST {{user_base}}/api/v1/profile/create
Content-Type: application/json
Authorization: Bearer {{user_access_token}}

{
  "userId": 1,
  "username": "testuser1",
  "email": "user1@test.com",
  "phone": "+1234567890",
  "address": "123 Main St, City, State 12345"
}
```

### 2.2 Get User Profile
```http
GET {{user_base}}/api/v1/profile/1
Authorization: Bearer {{user_access_token}}
```

### 2.3 Create Vendor Profile
```http
POST {{user_base}}/api/v1/profile/create
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}

{
  "userId": 2,
  "username": "vendor1", 
  "email": "vendor1@test.com",
  "phone": "+1987654321",
  "address": "456 Business Ave, City, State 67890"
}
```

---

## 🏗️ 3. Admin Setup - Booking Types

### 3.1 Create Futsal Booking Type
```http
POST {{metadata_base}}/api/v1/admin/booking-types
Content-Type: application/json
Authorization: Bearer {{admin_access_token}}
X-User-Role: ADMIN

{
  "name": "futsal",
  "description": "Indoor football court booking",
  "category": "Sports"
}
```

### 3.2 Create Conference Room Booking Type  
```http
POST {{metadata_base}}/api/v1/admin/booking-types
Content-Type: application/json
Authorization: Bearer {{admin_access_token}}
X-User-Role: ADMIN

{
  "name": "conference_room",
  "description": "Meeting room reservations", 
  "category": "Business"
}
```

### 3.3 Get All Booking Types (Use for reference)
```http
GET {{metadata_base}}/api/v1/booking-types
Authorization: Bearer {{admin_access_token}}
```

---

## 📝 4. Vendor Application Process

### 4.1 Vendor Apply for Futsal Service
```http
POST {{metadata_base}}/api/v1/vendor/apply
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}
X-User-Id: 2

{
  "vendorId": 2,
  "bookingTypeId": 1,
  "details": "Premium futsal court with artificial turf, floodlights, changing rooms, and parking. Available 6 AM to 11 PM daily."
}
```

### 4.2 Admin View Pending Applications
```http
GET {{metadata_base}}/api/v1/admin/vendor-applications/pending
Authorization: Bearer {{admin_access_token}}
X-User-Role: ADMIN
```

### 4.3 Admin Approve Vendor Application
```http
POST {{metadata_base}}/api/v1/admin/applications/1/approve
Content-Type: application/json
Authorization: Bearer {{admin_access_token}}
X-User-Role: ADMIN
```

### 4.4 Admin Reject Vendor Application (Alternative)
```http
POST {{metadata_base}}/api/v1/admin/applications/1/reject
Content-Type: application/json
Authorization: Bearer {{admin_access_token}}
X-User-Role: ADMIN
```

---

## 🏪 5. Vendor Details Setup

### 5.1 Add Vendor Metadata (User Service)
```http
POST {{user_base}}/api/v1/vendor-metadata
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}

{
  "vendorId": 2,
  "serviceName": "Elite Futsal Arena",
  "location": "Downtown Sports Complex, 789 Sports Blvd, City, State",
  "price": 75.00,
  "extraInfo": "Professional FIFA quality court, online booking system, equipment rental available",
  "images": "https://example.com/court1.jpg,https://example.com/court2.jpg",
  "availabilityWindow": "06:00-23:00"
}
```

### 5.2 Add Vendor Metadata (Metadata Service)
```http
POST {{metadata_base}}/api/v1/vendor-metadata
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}

{
  "vendorId": 2,
  "bookingTypeId": 1,
  "location": "Downtown Sports Complex - Court A",
  "price": 75.00, 
  "availabilityWindow": "06:00-23:00",
  "extraInfo": "Air conditioned, professional lighting, sound system",
  "images": "https://example.com/court1.jpg"
}
```

---

## 🔍 6. User Booking Flow

### 6.1 Search Available Time Slots (Direct Futsal Service)
```http
GET {{futsal_base}}/api/v1/futsal/availability?vendorId=2&date=2025-12-01
Authorization: Bearer {{user_access_token}}
```

### 6.2 Check Vendor Details (Metadata Service)
```http
GET {{metadata_base}}/api/v1/vendor-metadata?vendorId=2&bookingTypeId=1
Authorization: Bearer {{user_access_token}}
```

### 6.3 Create Booking via Orchestrator
```http
POST {{orchestrator_base}}/api/v1/bookings/create
Content-Type: application/json
Authorization: Bearer {{user_access_token}}
X-User-Id: 1

{
  "userId": 1,
  "vendorId": 2,
  "bookingType": "futsal",
  "bookingTypeId": 1,
  "dateTime": "2025-12-01T10:00:00+00:00",
  "durationMinutes": 90,
  "participants": 10,
  "price": 75.00,
  "extraInfo": "Birthday party - need decorations setup time"
}
```

### 6.4 Create Booking via API Gateway (Full Flow)
```http
POST {{gateway_base}}/api/v1/bookings/create  
Content-Type: application/json
Authorization: Bearer {{user_access_token}}

{
  "userId": 1,
  "vendorId": 2,
  "bookingType": "futsal", 
  "bookingTypeId": 1,
  "dateTime": "2025-12-01T14:00:00+00:00",
  "durationMinutes": 120,
  "participants": 12,
  "price": 100.00,
  "extraInfo": "Corporate team building event"
}
```

---

## ✅ 7. Vendor Booking Management

### 7.1 Get All Bookings for Vendor
```http
GET {{futsal_base}}/api/v1/futsal/bookings?vendorId=2
Authorization: Bearer {{vendor_access_token}}
X-User-Id: 2
```

### 7.2 Update Booking Status - Approve
```http
POST {{futsal_base}}/api/v1/futsal/1/status?status=APPROVED
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}
X-User-Id: 2
```

### 7.3 Update Booking Status - Reject  
```http
POST {{futsal_base}}/api/v1/futsal/1/status?status=REJECTED
Content-Type: application/json
Authorization: Bearer {{vendor_access_token}}
X-User-Id: 2
```

---

## 🧪 8. Health Check Endpoints

### 8.1 Service Health Checks
```http
GET {{auth_base}}/actuator/health
GET {{user_base}}/actuator/health  
GET {{metadata_base}}/actuator/health
GET {{futsal_base}}/actuator/health
GET {{orchestrator_base}}/api/v1/bookings/health
GET {{gateway_base}}/actuator/health
```

---

## 📊 9. Test Data Summary

### Users Created:
| ID | Username | Role | Email |
|----|----------|------|-------|
| 1 | testuser1 | USER | user1@test.com |
| 2 | vendor1 | VENDOR | vendor1@test.com |
| 3 | admin1 | ADMIN | admin@test.com |

### Booking Types:
| ID | Name | Category | Description |
|----|------|----------|-------------|
| 1 | futsal | Sports | Indoor football court |
| 2 | conference_room | Business | Meeting rooms |

### Vendor Applications:
| ID | VendorId | BookingTypeId | Status |
|----|----------|---------------|---------|
| 1 | 2 | 1 | APPROVED |

---

## 🚨 Known Issues & Workarounds

### Issue 1: UUID vs BIGINT Mismatch
**Problem:** Futsal service uses UUID but other services use BIGINT  
**Workaround:** Database conversion was applied, ensure futsal service uses Long IDs

### Issue 2: Duplicate vendor_metadata Tables
**Problem:** Both user-service and metadata-service have vendor_metadata  
**Workaround:** User-service table renamed to `user_vendor_metadata`

### Issue 3: DDL Mode Inconsistency  
**Problem:** Metadata service used create-drop mode
**Status:** ✅ Fixed - Changed to update mode

---

## 🔧 Environment Variables

Set these in Postman environment:
```
auth_base = http://localhost:8081
user_base = http://localhost:8083  
metadata_base = http://localhost:8084
orchestrator_base = http://localhost:8086
futsal_base = http://localhost:8087
gateway_base = http://localhost:8080

user_access_token = {{token_from_login}}
vendor_access_token = {{vendor_token_from_login}}
admin_access_token = {{admin_token_from_login}}
```

## 📝 Testing Checklist

- [ ] All user registrations successful
- [ ] All logins return valid JWT tokens
- [ ] User profiles created successfully  
- [ ] Admin can create booking types
- [ ] Vendor applications submit successfully
- [ ] Admin can approve vendor applications
- [ ] Vendor can add service metadata
- [ ] Users can search and book time slots
- [ ] Vendor can approve/reject bookings
- [ ] API Gateway routes requests correctly
- [ ] All database relationships maintained

**Test Order:** Follow sections 1-7 in sequence for complete workflow validation.

---

## 🚨 Critical Issues Identified & Fixed

### Issue 1: Database Schema Mismatches ✅ FIXED
**Problem:** UUID vs BIGINT ID types breaking foreign key relationships  
**Solution:** Converted futsal service from UUID to Long (BIGINT)  
**Impact:** All services now use consistent Long IDs

### Issue 2: Table Name Conflicts ✅ FIXED  
**Problem:** Both userservice and metadata-service had `vendor_metadata` tables  
**Solution:** Renamed userservice table to `user_vendor_metadata`  
**Impact:** No more schema conflicts

### Issue 3: DDL Mode Data Loss ✅ FIXED
**Problem:** Metadata service using `create-drop` causing data loss on restart  
**Solution:** Changed to `update` mode for data persistence  
**Impact:** Metadata and applications persist across service restarts

### Issue 4: Missing API Endpoints ⚠️ IDENTIFIED
**Missing Endpoints Needed:**
- `GET /api/v1/booking-types` (metadata service) - for listing booking types
- `GET /api/v1/futsal/availability` (futsal service) - for checking availability  
- `GET /api/v1/futsal/bookings` (futsal service) - for vendor booking management

### Issue 5: Authentication Headers ⚠️ CRITICAL
**Required Headers for Services:**
- **Metadata Service:** Requires `X-User-Id` and `X-User-Role: ADMIN` headers
- **User Service:** Requires `X-User-Id` header  
- **Futsal Service:** Requires `X-User-Id` header
- **API Gateway:** Automatically injects these headers from JWT tokens

### Issue 6: Missing Vendor Metadata Endpoints ⚠️ IDENTIFIED
**Conflict:** Both services have `/api/v1/vendor/metadata` endpoints
- **User Service:** For general vendor service information
- **Metadata Service:** For booking-type-specific vendor details

### Issue 7: Status Enum Values ⚠️ CHECK REQUIRED
**Futsal Service BookingStatus Enum:**
```java
// Verify these exact values are used:
PENDING, APPROVED, REJECTED, CANCELLED
```

**Vendor Application Status Enum:**
```java  
// Verify these exact values:
PENDING, APPROVED, REJECTED
```

---

## 🔧 Implementation Verification Needed

### Missing Service Implementations:
1. **BookingType List Endpoint** - Add to metadata service
2. **Futsal Availability Check** - Add to futsal service  
3. **Vendor Booking List** - Add to futsal service
4. **Proper Error Handling** - All services need consistent error responses

### Database Verification Required:
1. Confirm all foreign key relationships work with Long IDs
2. Test cascade operations and referential integrity  
3. Verify enum values match exactly between services
4. Check that metadata service data persists after restart

**CRITICAL:** Test the complete flow in the exact order provided to identify any remaining integration issues.