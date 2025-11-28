package com.jugger.metadataservice.dto;

import lombok.Data;

@Data
public class VendorApplicationRequest {
    private Long vendorId;
    private Long bookingTypeId;
    private String details;
}
