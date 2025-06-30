package com.rental.houserental.service;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.response.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    RentalProperty createProperty(CreatePropertyRequestDTO request, User landlord, MultipartFile[] imageFiles);
    
    List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit);
}
