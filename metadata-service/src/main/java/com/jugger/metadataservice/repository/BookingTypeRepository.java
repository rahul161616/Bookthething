package com.jugger.metadataservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jugger.metadataservice.model.BookingType;

public interface BookingTypeRepository extends JpaRepository<BookingType, Long> {
     boolean existsByName(String name);
}
