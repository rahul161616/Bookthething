package com.jugger.futsalservice.dto;

import lombok.Data;

@Data
public class FutsalBookingResponse {
    private Long bookingId;
    private String status;
    private String message;
}
