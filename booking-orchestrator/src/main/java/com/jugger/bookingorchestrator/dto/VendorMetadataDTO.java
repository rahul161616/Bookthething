package com.jugger.bookingorchestrator.dto;
    
import lombok.Data;

@Data
public class VendorMetadataDTO {
    private Long id;
    private Long vendorId;
    private Long bookingTypeId;
    private String serviceName;
    private String location;
    private Double price;
    private String extraInfo;
    private String availabilityWindow;
}