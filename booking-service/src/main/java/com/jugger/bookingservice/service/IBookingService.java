package com.jugger.bookingservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.jugger.bookingservice.dto.BookingDto;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.model.BookingType;

public interface IBookingService {

     BookingDto createBooking(
            UUID userId,
            UUID itemId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            BigDecimal price
    );

    List<BookingDto> getBookingsByUser(UUID userId);

    List<BookingDto> getBookingsByType(UUID userId, BookingType type);

    Optional<BookingDto> getBookingById(UUID bookingId, UUID userId);

    Booking updateBookingStatus(UUID bookingId, BookingStatus status);

}
