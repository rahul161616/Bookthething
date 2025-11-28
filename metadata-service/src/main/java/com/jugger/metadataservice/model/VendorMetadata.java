package com.jugger.metadataservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "vendor_metadata")
public class VendorMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vendorId; // FK to Auth Service User ID

    // private Long vendorId; // FK to auth-service user ID
    private Long bookingTypeId; // FK to BookingType

    private String location;
    private Double price;
    private String availabilityWindow; // JSON string or structured format
    private String extraInfo;
    private String images; // URLs
}
