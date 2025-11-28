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
        ResponseEntity<VendorMetadataDTO[]> resp = restTemplate.getForEntity(url, VendorMetadataDTO[].class);
        VendorMetadataDTO[] arr = resp.getBody();
        if (arr == null) return null;
        for (VendorMetadataDTO m : arr) {
            if (m.getBookingTypeId().equals(bookingTypeId)) return m;
        }
        return null;
    }

    public List<VendorMetadataDTO> getByType(Long bookingTypeId) {
        String url = metadataServiceBase + "/metadata?bookingTypeId=" + bookingTypeId;
        VendorMetadataDTO[] arr = restTemplate.getForObject(url, VendorMetadataDTO[].class);
        return arr == null ? Collections.emptyList() : Arrays.asList(arr);
    }
}
