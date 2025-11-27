package com.jugger.bookingorchestrator.client;

import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FutsalBookingClient {
     private final RestTemplate restTemplate;
    private final String serviceUrl = "http://localhost:8087/api/v1/futsal";

    public FutsalBookingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookingResponse createBooking(BookingRequest request) {
        return restTemplate.postForObject(serviceUrl + "/book", request, BookingResponse.class);
    }
}
