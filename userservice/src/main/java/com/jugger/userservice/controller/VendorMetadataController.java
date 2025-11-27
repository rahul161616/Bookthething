package com.jugger.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jugger.userservice.dto.VendorMetadataRequest;
import com.jugger.userservice.dto.VendorMetadataResponse;
import com.jugger.userservice.service.VendorMetadataService;

@RestController
@RequestMapping("/api/v1/vendor")
public class VendorMetadataController {

    private final VendorMetadataService service;
    
    public VendorMetadataController(VendorMetadataService service) {
        this.service = service;
    }

    @PostMapping("/metadata")
    public ResponseEntity<VendorMetadataResponse> addMetadata(@RequestBody VendorMetadataRequest request) {
        return ResponseEntity.ok(service.addMetadata(request));
    }

    @GetMapping("/{vendorId}/metadata")
    public ResponseEntity<List<VendorMetadataResponse>> getVendorMetadata(@PathVariable Long vendorId) {
        return ResponseEntity.ok(service.getVendorServices(vendorId));
    }  
}
