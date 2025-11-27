package com.jugger.userservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jugger.userservice.model.VendorMetadata;

public interface VendorMetadataRepository extends  JpaRepository<VendorMetadata, Long>{
     List<VendorMetadata> findByVendorId(Long vendorId);
}
