package com.rental.houserental.service;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.response.property.SearchPropertyResponseDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PropertyService {

    RentalProperty createProperty(CreatePropertyRequestDTO request, User landlord, MultipartFile[] imageFiles);// Default limit
    
    List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit);
    
    SearchPropertyResponseDTO getPropertyById(Long id);

    Page<SearchPropertyResponseDTO> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable);
}
