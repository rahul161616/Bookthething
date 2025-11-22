package com.jugger.bookingservice.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(
            BookingNotFoundException ex, 
            WebRequest request) {
        
        log.error("Booking not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .error("BOOKING_NOT_FOUND")
                .message(ex.getMessage())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, 
            WebRequest request) {
        
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false))
                .build();
                
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}