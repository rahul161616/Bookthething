package com.jugger.userservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_vendor_metadata")
public class VendorMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vendorId; // FK to User ID

    private String serviceName; // e.g., Futsal, Room, Event

    private String location;

    private Double price;

    private String extraInfo;

    private String images; // URLs

    private String availabilityWindow; // JSON or structured string
    
}
