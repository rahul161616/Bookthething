package com.jugger.futsalservice.service;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.jugger.futsalservice.dto.FutsalBookingRequest;
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

    public FutsalBookingResponse createBooking(UUID userId, FutsalBookingRequest request) {

        // Step 1: Check availability
        List<FutsalBooking> overlapping = repository
                    .findByVendorIdAndEndTimeAfterAndStartTimeBefore(
                        request.getVendorId(), 
                        request.getStartTime(), 
                        request.getEndTime());

        if (!overlapping.isEmpty()) {
            FutsalBookingResponse response = new FutsalBookingResponse();
            response.setMessage("Time slot not available");
            response.setStatus("FAILED");
            return response;
        }

        // Step 2: Save booking
        FutsalBooking booking = new FutsalBooking();
        booking.setUserId(userId);
        booking.setVendorId(request.getVendorId());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setParticipants(request.getParticipants());
        booking.setPrice(request.getPrice());
        booking.setStatus(BookingStatus.PENDING);

        repository.save(booking);
        // Return response
        FutsalBookingResponse response = new FutsalBookingResponse();
        response.setBookingId(booking.getId());
        response.setStatus(booking.getStatus().name());
        response.setMessage("Booking created successfully");
        return response;
    }
     // Optional: Approve/Reject booking by vendor
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
