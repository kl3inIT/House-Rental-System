package com.rental.houserental.service;

import com.rental.houserental.entity.Amenity;

import java.util.List;

public interface AmenityService {
    List<Amenity> findAll();
    Amenity findById(Long id);
    List<Amenity> findAllByIds(List<Long> ids);
}
