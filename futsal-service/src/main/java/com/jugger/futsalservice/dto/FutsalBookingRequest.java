package com.jugger.futsalservice.dto;

// import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class FutsalBookingRequest {

    private UUID userId;
    private UUID vendorId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private int participants;
    private double price; // optional for vendor-defined price
    
}
