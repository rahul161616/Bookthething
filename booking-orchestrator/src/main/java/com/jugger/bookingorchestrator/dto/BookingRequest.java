package com.jugger.bookingorchestrator.dto;

import lombok.Data;

@Data
public class BookingRequest {
    
    private Long userId;
    private String bookingType; // e.g., "Room", "Futsal", "Event"
    private Long vendorId;
    private Long bookingTypeId; // Required for metadata validation
    private String dateTime; // ISO 8601 string
    private Integer durationMinutes;
    private Double price;
    private String extraInfo;
    
}
