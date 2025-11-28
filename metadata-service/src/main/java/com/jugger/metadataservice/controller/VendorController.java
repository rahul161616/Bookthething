package com.jugger.metadataservice.controller;

import org.springframework.web.bind.annotation.*;
// import org.springframework.http.ResponseEntity;
import com.jugger.metadataservice.service.VendorService;
// import com.jugger.metadataservice.model.VendorMetadata;
import com.jugger.metadataservice.dto.VendorApplicationRequest;
import com.jugger.metadataservice.dto.VendorApplicationResponse;
import com.jugger.metadataservice.dto.VendorMetadataRequest;
import com.jugger.metadataservice.dto.VendorMetadataResponse;
// import com.jugger.metadataservice.model.VendorApplication;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendor")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @PostMapping("/apply")
    public VendorApplicationResponse apply(@RequestBody VendorApplicationRequest request) {
         return service.submitVendorApplication(request);
    }

    @PostMapping("/metadata")
    public VendorMetadataResponse addMetadata(@RequestBody VendorMetadataRequest request) {
        return service.addMetadata(request);
    }
   @GetMapping("/{vendorId}/metadata")
    public List<VendorMetadataResponse> getVendorMetadata(@PathVariable Long vendorId) {
        return service.getVendorMetadata(vendorId);
    }
}
