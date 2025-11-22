package com.jugger.bookingservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jugger.bookingservice.model.Booking;

public interface BookingRepository extends JpaRepository<Booking,UUID>{
     List<Booking> findByUserId(UUID userId);
}
