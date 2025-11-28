package com.jugger.metadataservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jugger.metadataservice.model.VendorApplication;
import com.jugger.metadataservice.model.VendorApplication.ApplicationStatus;

public interface VendorApplicationRepository extends JpaRepository<VendorApplication, Long> {
    List<VendorApplication> findByStatus(ApplicationStatus pending);
    List<VendorApplication> findByVendorId(Long vendorId);
}
