package com.jugger.bookingservice.mapper;

import org.springframework.stereotype.Component;

import com.jugger.bookingservice.dto.BookingResponseDto;
import com.jugger.bookingservice.dto.CreateBookingRequestDto;
import com.jugger.bookingservice.model.Booking;

@Component
public class BookingMapper {
    
    public Booking toEntity(CreateBookingRequestDto dto) {
        return Booking.builder()
                .userId(dto.getUserId())
                .itemId(dto.getItemId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .price(dto.getPrice())
                .build();
    }
    
    public BookingResponseDto toResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .itemId(booking.getItemId())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .price(booking.getPrice())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}