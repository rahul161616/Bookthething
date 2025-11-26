package com.jugger.bookingservice.mapper;

import org.springframework.stereotype.Component;

import com.jugger.bookingservice.dto.BookingDto;
import com.jugger.bookingservice.model.Booking;

@Component
public class BookingMapper {
    
     public static BookingDto toDto(Booking booking) {
          BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        // dto.setItemId(booking.getId()); // assuming each booking is linked to a specific item/resource
        dto.setBookingType(booking.getBookingType());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setPrice(booking.getPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        return dto;
    }
}