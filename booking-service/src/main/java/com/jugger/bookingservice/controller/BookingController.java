package com.jugger.bookingservice.controller;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
import java.util.List;
// import java.util.Map;
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
import com.jugger.bookingservice.mapper.BookingMapper;
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
            @RequestBody BookingDto requestDto) {

        try {
            UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
            if (userId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

            BookingType type = requestDto.getBookingType() != null
                    ? requestDto.getBookingType()
                    : BookingType.FUTSAL;

            BookingDto created = bookingService.createBooking(
                    userId,
                    requestDto.getStartTime(),
                    requestDto.getEndTime(),
                    requestDto.getPrice(),
                    type
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (Exception e) {
            log.error("Error creating booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

     // ------------------ Get All Bookings for User ------------------
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Username", required = false) String username) {
        
        UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        List<BookingDto> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    // ------------------ Get Booking by ID ------------------
     // Get all bookings (optional type filter)
    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Username", required = false) String username,
            @RequestParam(required = false) BookingType type) {

        UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
        if (userId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        List<BookingDto> bookings = (type != null)
                ? bookingService.getBookingsByType(userId, type)
                : bookingService.getBookingsByUser(userId);

        return ResponseEntity.ok(bookings);
    }
    
    // Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<BookingDto> getBookingById(
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestHeader(value = "X-Username", required = false) String username,
            @PathVariable UUID id) {

        UUID userId = getUserUUIDFromHeaders(userIdHeader, username);
        if (userId == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        return bookingService.getBookingById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

     // ------------------ Update Booking Status ------------------
    // Update status
    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable UUID bookingId,
            @RequestParam BookingStatus status) {

        try {
            Booking updated = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(BookingMapper.toDto(updated));
        } catch (Exception e) {
            log.error("Error updating booking status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
   // ------------------ Health Check ------------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Booking Service is up and running!");
    }

    // Helper method for UUID
    private UUID getUserUUIDFromHeaders(String userIdHeader, String username) {
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try { return UUID.fromString(userIdHeader); }
            catch (IllegalArgumentException e) { /* fallback below */ }
        }
        if (username != null && !username.isEmpty()) {
            return UUID.nameUUIDFromBytes(username.getBytes());
        }
        return null;
    }
}
