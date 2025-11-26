package com.jugger.bookingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.jugger.bookingservice.model.BookingStatus;
import com.jugger.bookingservice.model.BookingType;

import lombok.Data;

@Data
public class BookingDto {

    private UUID id;
    // private UUID itemId;
    private BookingType bookingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
}
