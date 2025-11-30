package com.jugger.userservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jugger.userservice.dto.VendorMetadataRequest;
import com.jugger.userservice.dto.VendorMetadataResponse;
import com.jugger.userservice.model.VendorMetadata;
import com.jugger.userservice.repository.VendorMetadataRepository;

@Service
public class VendorMetadataService {

    private final VendorMetadataRepository repo;
    public VendorMetadataService(VendorMetadataRepository repo) {
        this.repo = repo;
    }
    public VendorMetadataResponse addMetadata(VendorMetadataRequest request) {
        VendorMetadata metadata = new VendorMetadata();
        metadata.setVendorId(request.getVendorId());
        metadata.setServiceName(request.getServiceName());
        metadata.setLocation(request.getLocation());
        metadata.setPrice(request.getPrice());
        metadata.setExtraInfo(request.getExtraInfo());
        metadata.setImages(request.getImages());
        metadata.setAvailabilityWindow(request.getAvailabilityWindow());
        // Set new availability fields
        metadata.setDailySchedule(request.getDailySchedule());
        metadata.setBlockedDates(request.getBlockedDates());
        metadata.setSpecialDates(request.getSpecialDates());
        metadata.setSlotDurationMinutes(request.getSlotDurationMinutes());
        metadata.setAutoApproveBookings(request.getAutoApproveBookings());
        repo.save(metadata);
        return toResponse(metadata);
    }

    public VendorMetadataResponse updateAvailabilitySchedule(VendorMetadataRequest request) {
        // Find existing metadata for this vendor
        List<VendorMetadata> existing = repo.findByVendorId(request.getVendorId());
        VendorMetadata metadata;
        
        if (existing.isEmpty()) {
            // Create new if doesn't exist
            metadata = new VendorMetadata();
            metadata.setVendorId(request.getVendorId());
            metadata.setServiceName(request.getServiceName() != null ? request.getServiceName() : "Service");
        } else {
            // Update first existing record
            metadata = existing.get(0);
        }
        
        // Update availability fields
        if (request.getDailySchedule() != null) metadata.setDailySchedule(request.getDailySchedule());
        if (request.getBlockedDates() != null) metadata.setBlockedDates(request.getBlockedDates());
        if (request.getSpecialDates() != null) metadata.setSpecialDates(request.getSpecialDates());
        if (request.getSlotDurationMinutes() != null) metadata.setSlotDurationMinutes(request.getSlotDurationMinutes());
        if (request.getAutoApproveBookings() != null) metadata.setAutoApproveBookings(request.getAutoApproveBookings());
        if (request.getAvailabilityWindow() != null) metadata.setAvailabilityWindow(request.getAvailabilityWindow());
        
        repo.save(metadata);
        return toResponse(metadata);
    }
    public List<VendorMetadataResponse> getVendorServices(Long vendorId) {
        return repo.findByVendorId(vendorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
     private VendorMetadataResponse toResponse(VendorMetadata metadata) {
        VendorMetadataResponse response = new VendorMetadataResponse();
        response.setId(metadata.getId());
        response.setVendorId(metadata.getVendorId());
        response.setServiceName(metadata.getServiceName());
        response.setLocation(metadata.getLocation());
        response.setPrice(metadata.getPrice());
        response.setExtraInfo(metadata.getExtraInfo());
        response.setImages(metadata.getImages());
        response.setAvailabilityWindow(metadata.getAvailabilityWindow());
        // Add new availability fields to response
        response.setDailySchedule(metadata.getDailySchedule());
        response.setBlockedDates(metadata.getBlockedDates());
        response.setSpecialDates(metadata.getSpecialDates());
        response.setSlotDurationMinutes(metadata.getSlotDurationMinutes());
        response.setAutoApproveBookings(metadata.getAutoApproveBookings());
        return response;
    } 
}
