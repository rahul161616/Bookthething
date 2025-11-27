package com.jugger.futsalservice.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class FutsalBookingRequest {

   private UUID vendorId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int participants;
    private double price; // optional for vendor-defined price
    
}
