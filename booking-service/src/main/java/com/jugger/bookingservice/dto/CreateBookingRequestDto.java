package com.jugger.bookingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequestDto {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotNull(message = "Item ID is required")
    private UUID itemId;
    
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private String notes;
}