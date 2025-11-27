package com.jugger.futsalservice.controller;

import com.jugger.futsalservice.dto.FutsalBookingRequest;
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

   @PostMapping
    public ResponseEntity<FutsalBookingResponse> createBooking(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody FutsalBookingRequest request) {

        FutsalBookingResponse res = service.createBooking(userId, request);
        if ("FAILED".equals(res.getStatus())) return ResponseEntity.badRequest().body(res);
        return ResponseEntity.ok(res);
    }
    @PostMapping("/{bookingId}/status")
    public ResponseEntity<FutsalBookingResponse> updateStatus(
            @PathVariable UUID bookingId,
            @RequestParam BookingStatus status) {

        FutsalBookingResponse res = service.updateBookingStatus(bookingId, status);
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(res);
    }

}
