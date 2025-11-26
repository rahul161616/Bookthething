package com.jugger.bookingservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jugger.bookingservice.model.Booking;
import com.jugger.bookingservice.model.BookingType;

public interface BookingRepository extends JpaRepository<Booking,UUID>{
      List<Booking> findAllByUserId(UUID userId);
       List<Booking> findByUserIdAndBookingType(UUID userId, BookingType type);
       Optional<Booking> findByIdAndUserId(UUID id, UUID userId);
}
