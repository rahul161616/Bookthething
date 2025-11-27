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
                                                         @RequestHeader("X-User-Id") Long userId) {
        request.setUserId(userId); // JWT validated by Gateway
        return ResponseEntity.ok(service.createBooking(request));
    }
    
}
