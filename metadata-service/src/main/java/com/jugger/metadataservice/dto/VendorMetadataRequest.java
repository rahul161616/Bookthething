package com.jugger.metadataservice.dto;
import lombok.Data;

@Data
public class VendorMetadataRequest {
    private Long vendorId;
    private Long bookingTypeId;
    private String location;
    private Double price;
    private String availabilityWindow;
    private String extraInfo;
    private String images;
}
