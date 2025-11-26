package com.jugger.bookingservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
// import java.math.BigDecimal;
// import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jugger.bookingservice.dto.BookingDto;
import com.jugger.bookingservice.exception.BookingNotFoundException;
import com.jugger.bookingservice.mapper.BookingMapper;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.model.BookingType;
import com.jugger.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;

     // Create Booking
    public BookingDto createBooking(UUID userId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, BookingType bookingType) {
        Booking booking = Booking.builder()
                .userId(userId)
                .bookingType(bookingType)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .status(BookingStatus.PENDING)
                .build();
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }
     // Get all bookings for a user
    public List<BookingDto> getBookingsByUser(UUID userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
    //booking type filter
      public List<BookingDto> getBookingsByType(UUID userId, BookingType type) {
        return bookingRepository.findByUserIdAndBookingType(userId, type)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    // Update booking status
    public Booking updateBookingStatus(UUID bookingId, BookingStatus status) {
        log.info("Updating booking {} status to {}", bookingId, status);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated successfully", bookingId);
        return updatedBooking;
    }
    // Get booking by ID with user validation
      public Optional<BookingDto> getBookingById(UUID id, UUID userId) {
        return bookingRepository.findById(id)
                .filter(b -> b.getUserId().equals(userId))
                .map(BookingMapper::toDto);
    }

    // Get bookings by type
    public List<BookingDto> getBookingsByType(UUID userId, BookingType type) {
        return bookingRepository.findByUserIdAndBookingType(userId, type)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
