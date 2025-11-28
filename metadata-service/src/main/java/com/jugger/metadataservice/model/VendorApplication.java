package com.jugger.metadataservice.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "vendor_applications")
public class VendorApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vendorId; // FK to Auth Service User ID

    private Long bookingTypeId; // FK to BookingType
    @Column(nullable = false)
     private ApplicationStatus status = ApplicationStatus.PENDING; // PENDING, APPROVED, REJECTED

     private LocalDateTime appliedAt = LocalDateTime.now();
     private String details;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }

}
