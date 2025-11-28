package com.jugger.futsalservice.service;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jugger.futsalservice.dto.BookingRequestFromOrchestrator;
// import com.jugger.futsalservice.dto.FutsalBookingRequest;
import com.jugger.futsalservice.dto.FutsalBookingResponse;
import com.jugger.futsalservice.model.BookingStatus;
import com.jugger.futsalservice.model.FutsalBooking;
import com.jugger.futsalservice.repository.FutsalBookingRepository;

@Service
public class FutsalBookingService {

     private final FutsalBookingRepository repository;

    public FutsalBookingService(FutsalBookingRepository repository) {
        this.repository = repository;
    }

    public FutsalBookingResponse createFromOrchestrator(
            String xUserIdHeader,
            BookingRequestFromOrchestrator req
    ) {

        // vendorId from orchestrator is Long → convert to deterministic UUID
        UUID vendorUUID = new UUID(0L, req.getVendorId());

        // ---------------------------
        // Check availability
        // ---------------------------
        List<FutsalBooking> overlapping =
                repository.findByVendorIdAndEndTimeAfterAndStartTimeBefore(
                        vendorUUID,
                        req.getStartTime(),
                        req.getEndTime()
                );

        if (!overlapping.isEmpty()) {
            FutsalBookingResponse fail = new FutsalBookingResponse();
            fail.setStatus("FAILED");
            fail.setMessage("Time slot not available");
            return fail;
        }

        // ---------------------------
        // Create booking
        // ---------------------------
        FutsalBooking booking = new FutsalBooking();
        booking.setUserId(req.getUserId());            // already UUID from orchestrator
        booking.setVendorId(vendorUUID);
        booking.setStartTime(req.getStartTime());
        booking.setEndTime(req.getEndTime());
        booking.setParticipants(req.getParticipants() != null ? req.getParticipants() : 10);
        booking.setPrice(req.getPrice());
        booking.setStatus(BookingStatus.PENDING);

        repository.save(booking);

        // ---------------------------
        // Response
        // ---------------------------
        FutsalBookingResponse response = new FutsalBookingResponse();
        response.setBookingId(booking.getId());
        response.setStatus(booking.getStatus().name());
        response.setMessage("Booking created successfully");

        return response;
    }

    // ---------------------------
    // Vendor approve / reject
    // ---------------------------
    public FutsalBookingResponse updateBookingStatus(UUID bookingId, BookingStatus status) {
        FutsalBooking booking = repository.findById(bookingId).orElse(null);
        if (booking == null) return null;

        booking.setStatus(status);
        repository.save(booking);

        FutsalBookingResponse res = new FutsalBookingResponse();
        res.setBookingId(booking.getId());
        res.setStatus(booking.getStatus().name());
        res.setMessage("Booking status updated");

        return res;
    }
}
