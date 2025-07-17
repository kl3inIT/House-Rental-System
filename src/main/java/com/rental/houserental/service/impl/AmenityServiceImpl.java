package com.rental.houserental.service.impl;

import com.rental.houserental.entity.Amenity;
import com.rental.houserental.exceptions.common.ResourceNotFoundException;
import com.rental.houserental.repository.AmenityRepository;
import com.rental.houserental.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    @Override
    public List<Amenity> findAll() {
        return amenityRepository.findAll();
    }

    @Override
    public Amenity findById(Long id) {
        return amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
    }

    @Override
    public List<Amenity> findAllByIds(List<Long> ids) {
        return amenityRepository.findAllById(ids);
    }
}
