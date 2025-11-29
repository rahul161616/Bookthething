package com.jugger.futsalservice.repository;

import com.jugger.futsalservice.model.FutsalBooking;
import org.springframework.data.jpa.repository.JpaRepository;

// import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface FutsalBookingRepository extends JpaRepository<FutsalBooking, Long> {

    // Check if a vendor has availability for a time window
    List<FutsalBooking> findByVendorIdAndEndTimeAfterAndStartTimeBefore(Long vendorId, OffsetDateTime start, OffsetDateTime end);
}
