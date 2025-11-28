package com.jugger.metadataservice.service;

import org.springframework.stereotype.Service;

import com.jugger.metadataservice.dto.BookingTypeDto;
import com.jugger.metadataservice.model.BookingType;
import com.jugger.metadataservice.repository.BookingTypeRepository;

import java.util.List;

@Service
public class BookingTypeService {

    private final BookingTypeRepository repo;

    public BookingTypeService(BookingTypeRepository repo) {
        this.repo = repo;
    }

    public BookingType createBookingType(BookingTypeDto dto) {
        if(repo.existsByName(dto.getName())){
            throw new IllegalArgumentException("Booking type with name "+dto.getName()+" already exists.");
        }
        BookingType type = new BookingType();
        type.setName(dto.getName());
        type.setDescription(dto.getDescription());
        type.setCategory(dto.getCategory());
        return repo.save(type);
    }
    
    public List<BookingType> getAllBookingTypes() {
        return repo.findAll();
    }

    public BookingType getBookingType(Long id) {
        return repo.findById(id).orElse(null);
    }
}
