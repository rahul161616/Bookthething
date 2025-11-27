package com.jugger.futsalservice.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class FutsalBookingResponse {

   private UUID bookingId;
    private String status;
    private String message;
    
}
