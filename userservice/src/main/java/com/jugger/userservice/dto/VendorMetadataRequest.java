package com.jugger.userservice.dto;

import lombok.Data;

@Data
public class VendorMetadataRequest {

    private Long vendorId;
    private String serviceName;
    private String location;
    private Double price;
    private String extraInfo;
    private String images;
    private String availabilityWindow;
    
    // Enhanced availability scheduling
    private String dailySchedule; // JSON: {"monday":"06:00-23:00","sunday":"closed"}
    private String blockedDates; // JSON array: ["2025-12-25","2025-01-15"] 
    private String specialDates; // JSON: {"2025-12-31":"06:00-18:00"}
    private Integer slotDurationMinutes; // e.g., 90 minutes per slot
    private Boolean autoApproveBookings; // true/false for instant confirmation
    
}
