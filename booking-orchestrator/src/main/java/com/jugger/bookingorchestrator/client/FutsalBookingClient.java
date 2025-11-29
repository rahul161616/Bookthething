package com.jugger.bookingorchestrator.client;

import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FutsalBookingClient {
     private final RestTemplate restTemplate;
    private final String serviceUrl = "http://localhost:8087/api/v1/futsal";

    public FutsalBookingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookingResponse createBooking(BookingRequest request) {
        // Convert orchestrator format to futsal service format
        OffsetDateTime startTime = OffsetDateTime.parse(request.getDateTime());
        OffsetDateTime endTime = startTime.plusMinutes(request.getDurationMinutes());
        
        // Create the payload for futsal service
        Map<String, Object> futsalRequest = new HashMap<>();
        futsalRequest.put("userId", UUID.fromString("550e8400-e29b-41d4-a716-446655440" + String.format("%03d", request.getUserId())));
        futsalRequest.put("vendorId", request.getVendorId());
        futsalRequest.put("bookingType", request.getBookingType());
        futsalRequest.put("bookingTypeId", request.getBookingTypeId());
        futsalRequest.put("startTime", startTime);
        futsalRequest.put("endTime", endTime);
        futsalRequest.put("participants", 10); // Default participants
        futsalRequest.put("price", request.getPrice());
        futsalRequest.put("extraInfo", request.getExtraInfo());
        
        // Add required X-User-Id header for futsal service
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", request.getUserId().toString());
        headers.set("Content-Type", "application/json");
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(futsalRequest, headers);
        
        try {
            ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
                serviceUrl + "/book", HttpMethod.POST, entity, 
                (Class<Map<String, Object>>) (Class<?>) Map.class
            );
            
            Map<String, Object> futsalResp = resp.getBody();
            
            // Convert futsal response to orchestrator format
            BookingResponse response = new BookingResponse();
            response.setBookingId(null); // Futsal returns UUID, orchestrator expects Long
            response.setStatus((String) futsalResp.get("status"));
            response.setMessage((String) futsalResp.get("message"));
            
            return response;
        } catch (Exception e) {
            BookingResponse errorResponse = new BookingResponse();
            errorResponse.setStatus("FAILED");
            errorResponse.setMessage("Error calling futsal service: " + e.getMessage());
            return errorResponse;
        }
    }
}
