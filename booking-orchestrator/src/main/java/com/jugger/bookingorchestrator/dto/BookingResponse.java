package com.jugger.bookingorchestrator.dto;

import lombok.Data;

@Data
public class BookingResponse {

    private Long bookingId;
    private String status; // PENDING, APPROVED, REJECTED
    private String message;

}
