package com.jugger.bookingorchestrator.controller;

import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import com.jugger.bookingorchestrator.service.BookingOrchestratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    
    private final BookingOrchestratorService service;

    public BookingController(BookingOrchestratorService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request,
                                                         @RequestHeader("X-User-Id") String userIdHeader) {
        // Parse the UUID string to extract user ID - gateway sends UUID format
        // For simplicity, extract the user ID from the request body for now
        return ResponseEntity.ok(service.createBooking(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Booking Orchestrator is running");
    }
    
}
