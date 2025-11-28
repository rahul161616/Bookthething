package com.jugger.metadataservice.dto;

import com.jugger.metadataservice.model.VendorApplication.ApplicationStatus;

import lombok.Data;

@Data
public class VendorApplicationResponse {
    private Long id;
    private Long vendorId;
    private Long bookingTypeId;
    // private String bookingTypeName;
    private ApplicationStatus status;
    private String details;
}
