package com.jugger.bookingservice.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.bookingservice.dto.BookingDto;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.model.BookingType;
import com.jugger.bookingservice.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;

     // ------------------ Create Booking ------------------
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestBody Map<String, Object> request) {

        try {
            UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
            }
            
            UUID itemId = UUID.fromString(request.get("itemId").toString());
            LocalDateTime startTime = LocalDateTime.parse(request.get("startTime").toString());
            LocalDateTime endTime = LocalDateTime.parse(request.get("endTime").toString());
            BigDecimal price = new BigDecimal(request.get("price").toString());
            // Booking type
            String typeStr = request.getOrDefault("type", "STANDARD").toString();
            BookingType bookingType = BookingType.valueOf(typeStr.toUpperCase());

            Map<String, Object> metadata = (Map<String, Object>) request.getOrDefault("metadata", Map.of());
            BookingDto created = bookingService.createBooking(userId, itemId, startTime, endTime, price, bookingType,metadata);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            log.error("Error creating booking for user {}/{}: {}", userIdHeader, username, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }      
    }

      // ------------------ USER BOOKINGS ------------------
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        return ResponseEntity.ok(
                bookingService.getBookingsByUser(userId)
        );
    }

    // ------------------ GET ONE BOOKING ------------------
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(userIdHeader);

        return bookingService.getBookingById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // ------------------ UPDATE STATUS ------------------
    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(
            @PathVariable UUID id,
            @RequestParam BookingStatus status) {

        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

     // ------------------ HEALTH ------------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Booking Service alive!");
    }

    /**
     * Helper method to extract UUID from headers with fallback support
     * @param userIdHeader Direct UUID header value (X-User-Id)
     * @param username Fallback username for lookup (X-Username)
     * @return UUID representing the user
     */
    private UUID getUserUUIDFromHeaders(String userIdHeader, String username) {
        if (userIdHeader != null && !userIdHeader.trim().isEmpty()) {
            try {
                return UUID.fromString(userIdHeader);
            } catch (IllegalArgumentException e) {
                // If userIdHeader is not a valid UUID, fall back to username lookup
                // This could involve a service call to resolve username to UUID
                // For now, generate a deterministic UUID from username
                if (username != null) {
                    return UUID.nameUUIDFromBytes(username.getBytes());
                }
                throw new RuntimeException("Unable to determine user ID from headers");
            }
        } else if (username != null && !username.trim().isEmpty()) {
            // Generate deterministic UUID from username
            return UUID.nameUUIDFromBytes(username.getBytes());
        } else {
            throw new RuntimeException("No valid user identification found in headers");
        }
    }
}
