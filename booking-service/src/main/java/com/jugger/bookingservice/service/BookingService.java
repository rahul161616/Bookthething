package com.jugger.bookingservice.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jugger.bookingservice.exception.BookingNotFoundException;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        log.info("Creating new booking for user: {}", booking.getUserId());
        booking.setStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with ID: {}", savedBooking.getId());
        return savedBooking;
    }

    public List<Booking> getBookingsByUser(UUID userId) {
        log.info("Retrieving bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId);
    }

    public Booking updateBookingStatus(UUID bookingId, BookingStatus status) {
        log.info("Updating booking {} status to {}", bookingId, status);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} status updated successfully", bookingId);
        return updatedBooking;
    }
}
