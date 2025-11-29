package com.jugger.bookingorchestrator.util;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.OffsetDateTime;

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
            // Parse the requested time
            OffsetDateTime requestedTime = OffsetDateTime.parse(requestedDateTime);
            LocalTime requestedLocalTime = requestedTime.toLocalTime();
            
            // Parse availability window (e.g., "06:00-23:00")
            String[] timeParts = availabilityWindow.split("-");
            if (timeParts.length != 2) {
                return true; // Invalid format, assume always available
            }
            
            LocalTime startTime = LocalTime.parse(timeParts[0].trim());
            LocalTime endTime = LocalTime.parse(timeParts[1].trim());
            
            // Check if requested time is within availability window
            return !requestedLocalTime.isBefore(startTime) && !requestedLocalTime.isAfter(endTime);
            
        } catch (Exception e) {
            return false;
        }
    }
}