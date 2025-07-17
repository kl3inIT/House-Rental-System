package com.rental.houserental.service;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.request.property.LandlordPropertyFilterDTO;
import com.rental.houserental.dto.request.property.UpdatePropertyRequestDTO;
import com.rental.houserental.dto.response.property.*;
import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PropertyService {

    void createProperty(CreatePropertyRequestDTO request, User landlord, MultipartFile[] imageFiles);// Default limit
    
    List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit);
    
    SearchPropertyResponseDTO getPropertyById(Long id);

    Page<SearchPropertyResponseDTO> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable);

    Page<PropertyListItemDTO> searchPropertiesForLandlord(LandlordPropertyFilterDTO filter, Long landlordId, Pageable pageable);

    PropertyStatsDTO getPropertyStats(Long landlordId, LandlordPropertyFilterDTO filter);

    PropertyDetailDTO getPropertyDetailById(Long id);

    void updateProperty(Long id, UpdatePropertyRequestDTO dto, MultipartFile[] imageFiles);

    void deleteProperty(Long id);

    List<PropertyDetailDTO> getSimularProperties(Integer limit, Long propertyId);

    void increaseView(Long propertyId);

    List<RentalProperty> getPropertiesByLandlordStatusNotAvailable(Long landlordId);


}
