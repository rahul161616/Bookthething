package com.jugger.bookingorchestrator.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MetadataValidator {
    
    private static final double PRICE_TOLERANCE = 0.05; // 5% tolerance
    
    public static boolean isPriceAcceptable(Double requestedPrice, Double vendorPrice) {
        if (requestedPrice == null || vendorPrice == null) {
            return false;
        }
        
        double tolerance = vendorPrice * PRICE_TOLERANCE;
        return Math.abs(requestedPrice - vendorPrice) <= tolerance;
    }
    
    public static boolean isWithinAvailability(String requestedDateTime, String availabilityWindow) {
        if (requestedDateTime == null) {
            return false;
        }
        
        if (availabilityWindow == null || availabilityWindow.isEmpty()) {
            return true; // No restrictions means always available
        }
        
        try {
            LocalDateTime requestedTime = LocalDateTime.parse(requestedDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            // Simple validation - if availability window exists, assume it's valid
            // In production, you'd parse the availability window and check ranges
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}