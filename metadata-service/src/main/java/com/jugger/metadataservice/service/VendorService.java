package com.jugger.metadataservice.service;

// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.jugger.metadataservice.model.VendorApplication;
import com.jugger.metadataservice.model.VendorMetadata;
import com.jugger.metadataservice.dto.VendorApplicationRequest;
import com.jugger.metadataservice.dto.VendorApplicationResponse;
import com.jugger.metadataservice.dto.VendorMetadataRequest;
import com.jugger.metadataservice.dto.VendorMetadataResponse;
// import com.jugger.metadataservice.model.BookingType;
import com.jugger.metadataservice.repository.VendorApplicationRepository;
import com.jugger.metadataservice.repository.VendorMetadataRepository;
// import com.jugger.metadataservice.repository.BookingTypeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorService {

    private final VendorApplicationRepository appRepo;
    private final VendorMetadataRepository metaRepo;
    // private final BookingTypeRepository typeRepo;

    public VendorService(VendorApplicationRepository appRepo, VendorMetadataRepository metaRepo) {
        this.appRepo = appRepo;
        this.metaRepo = metaRepo;
    }

    // Vendor applies for approval
    public VendorApplicationResponse submitVendorApplication(VendorApplicationRequest request) {
        // BookingType type = typeRepo.findById(request.getBookingTypeId()).orElseThrow();
        VendorApplication app = new VendorApplication();
        app.setVendorId(request.getVendorId());
        app.setBookingTypeId(request.getBookingTypeId());
        app.setStatus(VendorApplication.ApplicationStatus.PENDING);
        app.setDetails(request.getDetails());
        VendorApplication savedApp = appRepo.save(app);  // Save to database
        return toApplicationResponse(savedApp);
    }
     public List<VendorApplicationResponse> getPendingApplications() {
        return appRepo.findByStatus(VendorApplication.ApplicationStatus.PENDING)
                .stream()
                .map(this::toApplicationResponse)
                .collect(Collectors.toList());
    }

    // Vendor adds metadata after approval
    public VendorMetadataResponse addMetadata(VendorMetadataRequest request) {
        // BookingType type = typeRepo.findById(request.getBookingTypeId()).orElseThrow();
        VendorMetadata meta = new VendorMetadata();
        meta.setVendorId(request.getVendorId());
        meta.setBookingTypeId(request.getBookingTypeId());
        meta.setLocation(request.getLocation());
        meta.setPrice(request.getPrice());
        meta.setAvailabilityWindow(request.getAvailabilityWindow());
        meta.setExtraInfo(request.getExtraInfo());
        meta.setImages(request.getImages());
        VendorMetadata savedMeta = metaRepo.save(meta);  // Save to database
        return toMetadataResponse(savedMeta);
    }

       public List<VendorMetadataResponse> getVendorMetadata(Long vendorId) {
        return metaRepo.findByVendorId(vendorId)
                .stream()
                .map(this::toMetadataResponse)
                .collect(Collectors.toList());
    }

    private VendorApplicationResponse toApplicationResponse(VendorApplication app) {
        VendorApplicationResponse resp = new VendorApplicationResponse();
        resp.setId(app.getId());
        resp.setVendorId(app.getVendorId());
        resp.setBookingTypeId(app.getBookingTypeId());
        resp.setDetails(app.getDetails());
        resp.setStatus(app.getStatus());
        return resp;
    }
    private VendorMetadataResponse toMetadataResponse(VendorMetadata meta) {
        VendorMetadataResponse resp = new VendorMetadataResponse();
        resp.setId(meta.getId());
        resp.setVendorId(meta.getVendorId());
        resp.setBookingTypeId(meta.getBookingTypeId());
        resp.setLocation(meta.getLocation());
        resp.setPrice(meta.getPrice());
        resp.setExtraInfo(meta.getExtraInfo());
        resp.setImages(meta.getImages());
        resp.setAvailabilityWindow(meta.getAvailabilityWindow());
        return resp;
    }

}
