package com.jugger.futsalservice.controller;

import com.jugger.futsalservice.dto.BookingRequestFromOrchestrator;
// import com.jugger.futsalservice.dto.FutsalBookingRequest;
import com.jugger.futsalservice.dto.FutsalBookingResponse;
import com.jugger.futsalservice.model.BookingStatus;
import com.jugger.futsalservice.service.FutsalBookingService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/futsal")
public class FutsalBookingController {

    
    private final FutsalBookingService service;

    public FutsalBookingController(FutsalBookingService service) {
        this.service = service;
    }
    /**
     * Orchestrator will POST unified BookingRequest (BookingRequestFromOrchestrator).
     * We accept X-User-Id header (string) - Gateway sets this.
     */
   @PostMapping("/book")
    public ResponseEntity<FutsalBookingResponse> createBooking(
            @RequestHeader("X-User-Id") String xUserId,
            @RequestBody BookingRequestFromOrchestrator request) {

        FutsalBookingResponse res = service.createFromOrchestrator(xUserId, request);
        if ("FAILED".equalsIgnoreCase(res.getStatus())) return ResponseEntity.badRequest().body(res);
        return ResponseEntity.status(201).body(res); //201 for resource created
    }
    @PostMapping("/{bookingId}/status")
    public ResponseEntity<FutsalBookingResponse> updateStatus(
            @PathVariable("bookingId") Long bookingId,
            @RequestParam("status") BookingStatus status) {

        FutsalBookingResponse res = service.updateBookingStatus(bookingId, status);
        if (res == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailability(
            @RequestParam("vendorId") Long vendorId,
            @RequestParam("date") String date) {
        
        try {
            // Call user service to get vendor availability data
            RestTemplate restTemplate = new RestTemplate();
            String userServiceUrl = "http://localhost:8083/api/v1/vendor/" + vendorId + "/metadata";
            
            // Make the API call to get vendor metadata
            ResponseEntity<Map[]> response = restTemplate.getForEntity(userServiceUrl, Map[].class);
            
            Map<String, Object> availability = new HashMap<>();
            availability.put("vendorId", vendorId);
            availability.put("date", date);
            
            if (response.getBody() != null && response.getBody().length > 0) {
                // Vendor has metadata - generate slots based on their schedule
                Map<String, Object> vendorMetadata = response.getBody()[0]; // Get first service
                availability.put("availableSlots", generateVendorAvailableSlots(date, vendorMetadata));
                availability.put("message", "Real availability for vendor " + vendorId + " on " + date);
                availability.put("source", "vendor-schedule");
                availability.put("vendorConfig", vendorMetadata);
            } else {
                // No vendor metadata found - return default slots
                availability.put("availableSlots", Arrays.asList("09:00", "11:00", "14:00", "16:00", "18:00"));
                availability.put("message", "Default availability (no vendor schedule set) for vendor " + vendorId + " on " + date);
                availability.put("source", "default");
            }
            
            return ResponseEntity.ok(availability);
            
        } catch (Exception e) {
            // Fallback to dummy data if user service is unavailable
            Map<String, Object> availability = new HashMap<>();
            availability.put("vendorId", vendorId);
            availability.put("date", date);
            availability.put("availableSlots", Arrays.asList("09:00", "11:00", "14:00", "16:00", "18:00"));
            availability.put("message", "Fallback availability (service error) for vendor " + vendorId + " on " + date);
            availability.put("source", "fallback");
            availability.put("error", e.getMessage());
            return ResponseEntity.ok(availability);
        }
    }
    
    private List<String> generateVendorAvailableSlots(String date, Map<String, Object> vendorMetadata) {
        List<String> slots = new ArrayList<>();
        
        try {
            // Extract vendor schedule information
            String dailyScheduleJson = (String) vendorMetadata.get("dailySchedule");
            String blockedDatesJson = (String) vendorMetadata.get("blockedDates");
            String specialDatesJson = (String) vendorMetadata.get("specialDates");
            Integer slotDuration = (Integer) vendorMetadata.get("slotDurationMinutes");
            
            // Check if the date is blocked
            if (blockedDatesJson != null && blockedDatesJson.contains("\"" + date + "\"")) {
                // Date is blocked, return empty slots
                return slots;
            }
            
            // Parse the day of week from date (simplified - assumes YYYY-MM-DD format)
            String[] dateParts = date.split("-");
            java.time.LocalDate localDate = java.time.LocalDate.of(
                Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]), 
                Integer.parseInt(dateParts[2])
            );
            String dayOfWeek = localDate.getDayOfWeek().toString().toLowerCase();
            
            // Check for special date schedule first
            String scheduleForDay = null;
            if (specialDatesJson != null && specialDatesJson.contains("\"" + date + "\"")) {
                // Extract special schedule (simplified parsing)
                int dateIndex = specialDatesJson.indexOf("\"" + date + "\"");
                int colonIndex = specialDatesJson.indexOf(":", dateIndex);
                int startQuote = specialDatesJson.indexOf("\"", colonIndex);
                int endQuote = specialDatesJson.indexOf("\"", startQuote + 1);
                if (startQuote != -1 && endQuote != -1) {
                    scheduleForDay = specialDatesJson.substring(startQuote + 1, endQuote);
                }
            }
            
            // If no special schedule, use daily schedule
            if (scheduleForDay == null && dailyScheduleJson != null) {
                if (dailyScheduleJson.contains("\"" + dayOfWeek + "\"")) {
                    int dayIndex = dailyScheduleJson.indexOf("\"" + dayOfWeek + "\"");
                    int colonIndex = dailyScheduleJson.indexOf(":", dayIndex);
                    int startQuote = dailyScheduleJson.indexOf("\"", colonIndex);
                    int endQuote = dailyScheduleJson.indexOf("\"", startQuote + 1);
                    if (startQuote != -1 && endQuote != -1) {
                        scheduleForDay = dailyScheduleJson.substring(startQuote + 1, endQuote);
                    }
                }
            }
            
            // Generate slots based on schedule
            if (scheduleForDay != null && !"closed".equals(scheduleForDay)) {
                // Parse schedule like "06:00-23:00"
                String[] timeParts = scheduleForDay.split("-");
                if (timeParts.length == 2) {
                    int duration = (slotDuration != null && slotDuration > 0) ? slotDuration : 90; // Default 90 minutes
                    
                    String[] startParts = timeParts[0].split(":");
                    String[] endParts = timeParts[1].split(":");
                    
                    int startHour = Integer.parseInt(startParts[0]);
                    int startMin = Integer.parseInt(startParts[1]);
                    int endHour = Integer.parseInt(endParts[0]);
                    int endMin = Integer.parseInt(endParts[1]);
                    
                    int startTotalMin = startHour * 60 + startMin;
                    int endTotalMin = endHour * 60 + endMin;
                    
                    // Generate slots
                    for (int currentMin = startTotalMin; currentMin + duration <= endTotalMin; currentMin += duration) {
                        int hour = currentMin / 60;
                        int min = currentMin % 60;
                        slots.add(String.format("%02d:%02d", hour, min));
                    }
                }
            }
            
            // If no schedule found or parsing failed, return default enhanced slots
            if (slots.isEmpty()) {
                slots.addAll(Arrays.asList("08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"));
            }
            
        } catch (Exception e) {
            // If parsing fails, return default slots
            slots.addAll(Arrays.asList("08:00", "10:00", "12:00", "14:00", "16:00", "18:00", "20:00"));
        }
        
        return slots;
    }
    
    private List<String> generateAvailableSlots(String date) {
        // Simple fallback method
        List<String> slots = new ArrayList<>();
        slots.add("08:00");
        slots.add("10:00"); 
        slots.add("12:00");
        slots.add("14:00");
        slots.add("16:00");
        slots.add("18:00");
        slots.add("20:00");
        slots.add("22:00");
        return slots;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Futsal service is running");
    }

}
