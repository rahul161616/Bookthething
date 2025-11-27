package com.jugger.bookingorchestrator.client;

import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EventBookingClient {


    private final RestTemplate restTemplate;
    private final String serviceUrl = "http://localhost:8085/api/v1/event-booking";

    public EventBookingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BookingResponse createBooking(BookingRequest request) {
        return restTemplate.postForObject(serviceUrl + "/book", request, BookingResponse.class);
    }
    
}
