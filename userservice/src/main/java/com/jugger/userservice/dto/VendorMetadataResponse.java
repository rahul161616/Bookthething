package com.jugger.userservice.dto;

import lombok.Data;

@Data
public class VendorMetadataResponse {
    private Long id;
    private Long vendorId;
    private String serviceName;
    private String location;
    private Double price;
    private String extraInfo;
    private String images;
    private String availabilityWindow;
}
