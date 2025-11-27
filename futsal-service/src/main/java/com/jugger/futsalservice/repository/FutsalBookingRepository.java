package com.jugger.futsalservice.repository;

import com.jugger.futsalservice.model.FutsalBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface FutsalBookingRepository extends JpaRepository<FutsalBooking, UUID> {

    // Check if a vendor has availability for a time window
    List<FutsalBooking> findByVendorIdAndEndTimeAfterAndStartTimeBefore(UUID vendorId, LocalDateTime endTime, LocalDateTime startTime
    );
}
