package com.jugger.bookingservice.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.bookingservice.dto.BookingResponseDto;
import com.jugger.bookingservice.dto.CreateBookingRequestDto;
import com.jugger.bookingservice.mapper.BookingMapper;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody CreateBookingRequestDto request) {
        log.info("Creating booking for user: {}", request.getUserId());
        Booking booking = bookingMapper.toEntity(request);
        Booking createdBooking = bookingService.createBooking(booking);
        BookingResponseDto response = bookingMapper.toResponseDto(createdBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByUser(@PathVariable UUID userId) {
        log.info("Retrieving bookings for user: {}", userId);
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        List<BookingResponseDto> response = bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{bookingId}/status")
    public ResponseEntity<BookingResponseDto> updateBookingStatus(@PathVariable UUID bookingId,
                                                       @RequestParam BookingStatus status) {
        log.info("Updating booking {} status to {}", bookingId, status);
        Booking updatedBooking = bookingService.updateBookingStatus(bookingId, status);
        BookingResponseDto response = bookingMapper.toResponseDto(updatedBooking);
        return ResponseEntity.ok(response);
    } 
    @GetMapping("/test")
    public String test(){
        return "Booking Service is up and running!";
    }
}
