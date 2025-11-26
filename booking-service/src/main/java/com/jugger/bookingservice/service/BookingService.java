package com.jugger.bookingservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
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
    public BookingDto createBooking(@NonNull UUID userId, @NonNull UUID itemId, @NonNull LocalDateTime startTime, @NonNull LocalDateTime endTime, @NonNull BigDecimal price, @NonNull BookingType bookingType, Map<String, Object> metadata) {
        Booking booking = Booking.builder()
                .userId(userId)
                .itemId(itemId)
                .bookingType(bookingType)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .metadata(metadata)
                .status(BookingStatus.PENDING)
                .build();
        Booking saved = bookingRepository.save(booking);
        return BookingMapper.toDto(saved);
    }

    // Get all bookings for a user
    public List<BookingDto> getBookingsByUser(@NonNull UUID userId) {
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    // Get bookings by type filter
    public List<BookingDto> getBookingsByType(@NonNull UUID userId, @NonNull BookingType type) {
        return bookingRepository.findByUserIdAndBookingType(userId, type)
                .stream()
                .map(BookingMapper::toDto)
                .toList();
    }

    // Update booking status
    public Booking updateBookingStatus(@NonNull UUID bookingId, @NonNull BookingStatus status) {
        log.info("Updating booking {} status to {}", bookingId, status);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated successfully", bookingId);
        return updatedBooking;
    }
    // Get booking by ID with user validation
    public Optional<BookingDto> getBookingById(@NonNull UUID id, @NonNull UUID userId) {
        return bookingRepository.findById(id)
                .filter(b -> b.getUserId().equals(userId))
                .map(BookingMapper::toDto);
    }
}
