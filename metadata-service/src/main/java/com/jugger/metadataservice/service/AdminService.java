package com.jugger.metadataservice.service;

import org.springframework.stereotype.Service;
import com.jugger.metadataservice.model.VendorApplication;
import com.jugger.metadataservice.repository.VendorApplicationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final VendorApplicationRepository appRepo;

    public AdminService(VendorApplicationRepository appRepo) {
        this.appRepo = appRepo;
    }

    public List<VendorApplication> getPendingApplications() {
        return appRepo.findByStatus(VendorApplication.ApplicationStatus.PENDING);
    }

     // Admin approves/rejects vendor application
    public VendorApplication approve(Long applicationId) {
        Optional<VendorApplication> opt = appRepo.findById(applicationId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Application not found: " + applicationId);
        VendorApplication app = opt.get();
        app.setStatus(VendorApplication.ApplicationStatus.APPROVED);
        return appRepo.save(app);
    }

    public VendorApplication reject(Long applicationId) {
        Optional<VendorApplication> opt = appRepo.findById(applicationId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Application not found: " + applicationId);
        VendorApplication app = opt.get();
        app.setStatus(VendorApplication.ApplicationStatus.REJECTED);
        return appRepo.save(app);
    }
}
