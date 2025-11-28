package com.jugger.futsalservice.controller;

import com.jugger.futsalservice.dto.BookingRequestFromOrchestrator;
// import com.jugger.futsalservice.dto.FutsalBookingRequest;
import com.jugger.futsalservice.dto.FutsalBookingResponse;
import com.jugger.futsalservice.model.BookingStatus;
import com.jugger.futsalservice.service.FutsalBookingService;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/futsal")
public class FutsalBookingController {

    
    private final FutsalBookingService service;

    public FutsalBookingController(FutsalBookingService service) {
        this.service = service;
    }
    /**
     * Orchestrator will POST unified BookingRequest (BookingRequestFromOrchestrator).
     * We accept X-User-Id header (string) - Gateway sets this.
     */
   @PostMapping("/book")
    public ResponseEntity<FutsalBookingResponse> createBooking(
            @RequestHeader("X-User-Id") String xUserId,
            @RequestBody BookingRequestFromOrchestrator request) {

        FutsalBookingResponse res = service.createFromOrchestrator(xUserId, request);
        if ("FAILED".equalsIgnoreCase(res.getStatus())) return ResponseEntity.badRequest().body(res);
        return ResponseEntity.status(201).body(res); //201 for resource created
    }
    @PostMapping("/{bookingId}/status")
    public ResponseEntity<FutsalBookingResponse> updateStatus(
            @PathVariable("bookingId") UUID bookingId,
            @RequestParam("status") BookingStatus status) {

        FutsalBookingResponse res = service.updateBookingStatus(bookingId, status);
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Futsal service is running");
    }

}
