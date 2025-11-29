package com.jugger.futsalservice.model;

// import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "futsal_bookings")
public class FutsalBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

     @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

   @Column(nullable = false)
    private int participants;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(nullable = false)
    private double price;
    
     @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
}