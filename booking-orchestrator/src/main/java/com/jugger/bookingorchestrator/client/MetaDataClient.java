package com.jugger.bookingorchestrator.client;

// import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
// import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jugger.bookingorchestrator.dto.VendorMetadataDTO;

@Component
public class MetaDataClient {

    private final RestTemplate restTemplate;
    // Local development host
    private final String metadataServiceBase = "http://localhost:8084/api/v1";

    public MetaDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Get metadata for a vendor filtered by bookingTypeId.
     * Cached for 30 seconds by orchestrator (see caching config).
     */
    @Cacheable(value = "vendorMetadata", key = "#vendorId + '-' + #bookingTypeId", unless="#result==null")
    public VendorMetadataDTO getVendorMetadata(Long vendorId, Long bookingTypeId) {
        String url = metadataServiceBase + "/vendor/" + vendorId + "/metadata";
        
        // Add required X-User-Id header for metadata service
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "1"); // System user for orchestrator
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<VendorMetadataDTO[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, VendorMetadataDTO[].class);
        VendorMetadataDTO[] arr = resp.getBody();
        if (arr == null) return null;
        for (VendorMetadataDTO m : arr) {
            if (m.getBookingTypeId().equals(bookingTypeId)) return m;
        }
        return null;
    }

    public List<VendorMetadataDTO> getByType(Long bookingTypeId) {
        String url = metadataServiceBase + "/metadata?bookingTypeId=" + bookingTypeId;
        
        // Add required X-User-Id header for metadata service
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "1"); // System user for orchestrator
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<VendorMetadataDTO[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, VendorMetadataDTO[].class);
        VendorMetadataDTO[] arr = resp.getBody();
        return arr == null ? Collections.emptyList() : Arrays.asList(arr);
    }
}
