package com.jugger.futsalservice.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class BookingRequestFromOrchestrator {

    private Long userId;          // Long from Orchestrator
    private Long vendorId;        // vendor ID (numeric from Metadata-Service)
    private String bookingType;   // "futsal"
    private Long bookingTypeId;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private Integer participants;
    private Double price;

    private String extraInfo; // optional
}
