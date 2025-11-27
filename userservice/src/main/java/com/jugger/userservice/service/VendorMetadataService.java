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
        return response;
    } 
}
