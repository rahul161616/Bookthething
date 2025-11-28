package com.jugger.metadataservice.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.jugger.metadataservice.model.VendorMetadata;

public interface VendorMetadataRepository extends JpaRepository<VendorMetadata, Long> {
    List<VendorMetadata> findByVendorId(Long vendorId);
    List<VendorMetadata> findByBookingTypeId(Long bookingTypeId);
}
