package com.jugger.bookingorchestrator.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jugger.bookingorchestrator.dto.UserProfileResponse;
import com.jugger.bookingorchestrator.dto.VendorMetadataDTO;

@Component
public class UserServiceClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl = "http://localhost:8083/api/v1";

    public UserServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UserProfileResponse getUserProfile(Long userId) {
        try {
            return restTemplate.getForObject(userServiceUrl + "/profile/" + userId, UserProfileResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    public VendorMetadataDTO getVendorMetadata(Long vendorId, String serviceName) {
        try {
            VendorMetadataDTO[] allServices = restTemplate.getForObject(
                    userServiceUrl + "/vendor/" + vendorId + "/metadata", VendorMetadataDTO[].class
            );
            if (allServices != null) {
                for (VendorMetadataDTO v : allServices) {
                    if (v.getServiceName().equalsIgnoreCase(serviceName)) return v;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}