package com.jugger.bookingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.jugger.bookingservice.model.BookingStatus;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private UUID id;
    private UUID userId;
    private UUID itemId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private BookingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}