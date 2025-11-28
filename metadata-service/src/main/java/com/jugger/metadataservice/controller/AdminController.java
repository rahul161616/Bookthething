package com.jugger.metadataservice.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.jugger.metadataservice.service.AdminService;
import com.jugger.metadataservice.service.BookingTypeService;
import com.jugger.metadataservice.dto.BookingTypeDto;
import com.jugger.metadataservice.model.BookingType;
import com.jugger.metadataservice.model.VendorApplication;

import java.util.List;


@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;
    private final BookingTypeService typeService;

    public AdminController(AdminService adminService, BookingTypeService typeService) {
        this.adminService = adminService;
        this.typeService = typeService;
    }
    @PostMapping("/booking-types")
    public BookingType createBookingType(@RequestBody BookingTypeDto dto) {
        return typeService.createBookingType(dto);
    }

    @GetMapping("/vendor-applications/pending")
    public ResponseEntity<List<VendorApplication>> getPendingApplications() {
        return ResponseEntity.ok(adminService.getPendingApplications());
    }

    @PostMapping("/applications/{id}/approve")
    public VendorApplication approveVendor(@PathVariable Long id) {
        return adminService.approve(id);
    }

    @PostMapping("/applications/{id}/reject")
    public VendorApplication rejectVendor(@PathVariable Long id) {
        return adminService.reject(id);
    }
}
