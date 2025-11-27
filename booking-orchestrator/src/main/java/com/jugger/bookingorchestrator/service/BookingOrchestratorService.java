package com.jugger.bookingorchestrator.service;

import org.springframework.stereotype.Service;

import com.jugger.bookingorchestrator.client.EventBookingClient;
import com.jugger.bookingorchestrator.client.FutsalBookingClient;
import com.jugger.bookingorchestrator.client.RoomBookingClient;
import com.jugger.bookingorchestrator.client.UserServiceClient;
import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import com.jugger.bookingorchestrator.dto.VendorMetadataDTO;

@Service
public class BookingOrchestratorService {

    private final UserServiceClient userServiceClient;
    private final RoomBookingClient roomBookingClient;
    private final EventBookingClient eventBookingClient;
    private final FutsalBookingClient futsalBookingClient;

    public BookingOrchestratorService(UserServiceClient userServiceClient,
                                      RoomBookingClient roomBookingClient,
                                      EventBookingClient eventBookingClient,
                                      FutsalBookingClient futsalBookingClient) {
        this.userServiceClient = userServiceClient;
        this.roomBookingClient = roomBookingClient;
        this.eventBookingClient = eventBookingClient;
        this.futsalBookingClient = futsalBookingClient;
    }

    public BookingResponse createBooking(BookingRequest request) {
        // Step 1: Validate user exists
        if (userServiceClient.getUserProfile(request.getUserId()) == null) {
            BookingResponse response = new BookingResponse();
            response.setStatus("REJECTED");
            response.setMessage("User not found");
            return response;
        }

        // Step 2: Get vendor metadata
        VendorMetadataDTO metadata = userServiceClient.getVendorMetadata(request.getVendorId(), request.getBookingType());
        if (metadata == null) {
            BookingResponse response = new BookingResponse();
            response.setStatus("REJECTED");
            response.setMessage("Vendor or service not found");
            return response;
        }

        // Step 3: Forward booking to correct service
        BookingResponse response;
        switch (request.getBookingType().toLowerCase()) {
            case "room":
                response = roomBookingClient.createBooking(request);
                break;
            case "event":
                response = eventBookingClient.createBooking(request);
                break;
            // Add more cases like FoodBooking later
            case "futsal":
                response = futsalBookingClient.createBooking(request);
                break;
            default:
                response = new BookingResponse();
                response.setStatus("REJECTED");
                response.setMessage("Unknown booking type");
                break;
        }
        
        // Optional: save audit log, trigger notifications
        return response;
    }
}