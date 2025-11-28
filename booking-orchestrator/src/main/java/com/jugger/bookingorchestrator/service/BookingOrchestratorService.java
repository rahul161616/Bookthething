package com.jugger.bookingorchestrator.service;

import org.springframework.stereotype.Service;

import com.jugger.bookingorchestrator.client.EventBookingClient;
import com.jugger.bookingorchestrator.client.FutsalBookingClient;
import com.jugger.bookingorchestrator.client.RoomBookingClient;
import com.jugger.bookingorchestrator.client.UserServiceClient;
import com.jugger.bookingorchestrator.client.MetaDataClient;
import com.jugger.bookingorchestrator.dto.BookingRequest;
import com.jugger.bookingorchestrator.dto.BookingResponse;
import com.jugger.bookingorchestrator.dto.UserProfileResponse;
import com.jugger.bookingorchestrator.dto.VendorMetadataDTO;
import com.jugger.bookingorchestrator.util.MetadataValidator;

@Service
public class BookingOrchestratorService {

    private final UserServiceClient userServiceClient;
    private final MetaDataClient metaDataClient;
    private final FutsalBookingClient futsalBookingClient;
    private final RoomBookingClient roomBookingClient;
    private final EventBookingClient eventBookingClient;

    public BookingOrchestratorService(UserServiceClient userServiceClient, 
                                    MetaDataClient metaDataClient,
                                    FutsalBookingClient futsalBookingClient,
                                    RoomBookingClient roomBookingClient,
                                    EventBookingClient eventBookingClient) {
        this.userServiceClient = userServiceClient;
        this.metaDataClient = metaDataClient;
        this.futsalBookingClient = futsalBookingClient;
        this.roomBookingClient = roomBookingClient;
        this.eventBookingClient = eventBookingClient;
    }

    public BookingResponse createBooking(BookingRequest request) {

        BookingResponse response = new BookingResponse();

        // 1) Validate user exists
        UserProfileResponse user = userServiceClient.getUserProfile(request.getUserId());
        if (user == null) {
            response.setStatus("REJECTED");
            response.setMessage("User not found");
            return response;
        }

        // 2) retrieve metadata (bookingTypeId MUST be set in request)
        VendorMetadataDTO metadata = metaDataClient.getVendorMetadata(request.getVendorId(), request.getBookingTypeId());
        if (metadata == null) {
            response.setStatus("REJECTED");
            response.setMessage("Vendor metadata not found for vendorId/bookingTypeId");
            return response;
        }

        // 3) Price check
        if (!MetadataValidator.isPriceAcceptable(request.getPrice(), metadata.getPrice())) {
            response.setStatus("REJECTED");
            response.setMessage("Price mismatch: requested price not within allowed tolerance");
            return response;
        }

        // 4) Availability check
        if (!MetadataValidator.isWithinAvailability(request.getDateTime(), metadata.getAvailabilityWindow())) {
            response.setStatus("REJECTED");
            response.setMessage("Requested date/time outside vendor availability");
            return response;
        }

        // 5) Forward to specific service
        BookingResponse downstreamResp;
        String type = request.getBookingType().toLowerCase();
        
        switch (type) {
            case "room":
                downstreamResp = roomBookingClient.createBooking(request);
                break;
            case "event":
                downstreamResp = eventBookingClient.createBooking(request);
                break;
            case "futsal":
                downstreamResp = futsalBookingClient.createBooking(request);
                break;
            default:
                response.setStatus("REJECTED");
                response.setMessage("Unknown booking type: " + request.getBookingType());
                return response;
        }

        // Return the downstream response
        return downstreamResp;
    }
}
