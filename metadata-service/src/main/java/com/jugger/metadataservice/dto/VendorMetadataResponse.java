package com.jugger.metadataservice.dto;

import lombok.Data;

@Data
public class VendorMetadataResponse {
    private Long id;
    private Long vendorId;
    private Long bookingTypeId;
    private String bookingTypeName;
    private String location;
    private Double price;
    private String availabilityWindow;
    private String extraInfo;
    private String images;
}
