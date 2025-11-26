package com.jugger.bookingservice.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.jugger.bookingservice.dto.BookingDto;
import com.jugger.bookingservice.mapper.BookingMapper;
import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.model.BookingType;
import com.jugger.bookingservice.repository.BookingRepository;

public class BookingServiceImpl implements IBookingService {

    @Override
    public BookingDto createBooking(UUID userId, UUID itemId, LocalDateTime startTime, LocalDateTime endTime,
            BigDecimal price) {
        // Determine booking type based on itemId prefix or external logic.
        BookingType type = BookingType.GENERAL; // default
        // Later you can change this logic per your project needs.

        Booking booking = Booking.builder()
                .userId(userId)
                .bookingType(type)
                .startTime(startTime)
                .endTime(endTime)
                .price(price)
                .status(BookingStatus.PENDING)
                .build();

        Booking saved;
        try {
            saved = BookingRepository.save(booking);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // log.info("Booking created for user {} with ID {}", userId, saved.getId());

        return BookingMapper.toDto(saved);
    }

    @Override
    public List<BookingDto> getBookingsByUser(UUID userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookingsByUser'");
    }

    @Override
    public List<BookingDto> getBookingsByType(UUID userId, BookingType type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookingsByType'");
    }

    @Override
    public Optional<BookingDto> getBookingById(UUID bookingId, UUID userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBookingById'");
    }

    @Override
    public Booking updateBookingStatus(UUID bookingId, BookingStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateBookingStatus'");
    }

    
    
}
