package com.jugger.bookingorchestrator.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
            // Add required X-User-Id header for user service
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", userId.toString());
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<UserProfileResponse> resp = restTemplate.exchange(
                userServiceUrl + "/profile/" + userId, HttpMethod.GET, entity, UserProfileResponse.class
            );
            return resp.getBody();
        } catch (Exception e) {
            return null;
        }
    }

    public VendorMetadataDTO getVendorMetadata(Long vendorId, String serviceName) {
        try {
            // Add required X-User-Id header for user service
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-User-Id", "1"); // System user for orchestrator
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<VendorMetadataDTO[]> resp = restTemplate.exchange(
                userServiceUrl + "/vendor/" + vendorId + "/metadata", HttpMethod.GET, entity, VendorMetadataDTO[].class
            );
            VendorMetadataDTO[] allServices = resp.getBody();
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