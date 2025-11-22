package com.jugger.bookingservice.exception;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class BookingNotFoundException extends RuntimeException {
    private final UUID bookingId;
    private final LocalDateTime timestamp;
    
    public BookingNotFoundException(UUID bookingId) {
        super(String.format("Booking with ID %s not found", bookingId));
        this.bookingId = bookingId;
        this.timestamp = LocalDateTime.now();
    }
}