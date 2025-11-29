package com.jugger.futsalservice.dto;

// import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class FutsalBookingRequest {

    private Long userId;
    private Long vendorId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private int participants;
    private double price; // optional for vendor-defined price
    
}
