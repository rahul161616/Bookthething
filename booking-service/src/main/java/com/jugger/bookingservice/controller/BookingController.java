package com.jugger.bookingservice.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

import lombok.Data;
// import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@Data
// @RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    // ------------------ Create Booking ------------------
    @PostMapping
    public ResponseEntity<BookingDto> createBooking(
            @RequestHeader("X-User-Id") String userIdHeader,
            @RequestBody Map<String, String> request) {

        try {
            // API Gateway already validated JWT - trust the injected headers
            UUID userId = UUID.fromString(userIdHeader);
            
            // Extract and validate booking parameters
            String itemIdStr = request.get("itemId");
            String startTimeStr = request.get("startTime");
            String endTimeStr = request.get("endTime");
            String priceStr = request.get("price");
            String typeStr = request.getOrDefault("type", "FUTSAL");
            
            if (itemIdStr == null || startTimeStr == null || endTimeStr == null || priceStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            
            UUID itemId = UUID.fromString(itemIdStr);
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
            BigDecimal price = new BigDecimal(priceStr);
            BookingType bookingType = BookingType.valueOf(typeStr);

            BookingDto created = bookingService.createBooking(
                Objects.requireNonNull(userId, "User ID cannot be null"),
                Objects.requireNonNull(itemId, "Item ID cannot be null"), 
                Objects.requireNonNull(startTime, "Start time cannot be null"),
                Objects.requireNonNull(endTime, "End time cannot be null"),
                Objects.requireNonNull(price, "Price cannot be null"),
                Objects.requireNonNull(bookingType, "Booking type cannot be null"),
                Map.of()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            System.err.println("Error creating booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }      
    }

    // ------------------ USER BOOKINGS ------------------
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader("X-User-Id") String userIdHeader) {
        
        try {
            // API Gateway already validated JWT - trust the injected headers
            UUID userId = UUID.fromString(userIdHeader);
            
            return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
        } catch (Exception e) {
            System.err.println("Error fetching user bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ------------------ GET ONE BOOKING ------------------
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable UUID id) {

        try {
            // API Gateway already validated JWT - trust the injected headers
            UUID userId = UUID.fromString(userIdHeader);

            return bookingService.getBookingById(id, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            System.err.println("Error fetching booking: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ------------------ UPDATE STATUS ------------------
    @PutMapping("/{id}/status")
    public ResponseEntity<Booking> updateStatus(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable UUID id,
            @RequestParam BookingStatus status) {

        try {
            // API Gateway already validated JWT - trust the injected headers
            UUID userId = UUID.fromString(userIdHeader);

            // Verify the booking belongs to the authenticated user
            Optional<BookingDto> existingBooking = bookingService.getBookingById(id, userId);
            if (existingBooking.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
        } catch (Exception e) {
            System.err.println("Error updating booking status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // ------------------ HEALTH ------------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Booking Service alive!");
    }
}