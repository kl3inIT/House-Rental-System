package com.rental.houserental.service;

import com.rental.houserental.dto.dashboard.PropertyPerformanceDTO;
import com.rental.houserental.dto.dashboard.RecentInquiryDTO;
import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.request.property.LandlordPropertyFilterDTO;
import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.request.property.UpdatePropertyRequestDTO;
import com.rental.houserental.dto.response.property.*;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyService {

    void createProperty(CreatePropertyRequestDTO request, User landlord, MultipartFile[] imageFiles);// Default limit

    List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit);

    SearchPropertyResponseDTO getPropertyById(Long id);

    Page<SearchPropertyResponseDTO> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable);

    Page<PropertyListItemDTO> searchPropertiesForLandlord(LandlordPropertyFilterDTO filter, Long landlordId,
            Pageable pageable);

    PropertyStatsDTO getPropertyStats(Long landlordId, LandlordPropertyFilterDTO filter);

    PropertyDetailDTO getPropertyDetailById(Long id);

    void updateProperty(Long id, UpdatePropertyRequestDTO dto, MultipartFile[] imageFiles);

    void deleteProperty(Long id);

    List<PropertyDetailDTO> getSimularProperties(Integer limit, Long propertyId);

    void increaseView(Long propertyId);

    List<RentalProperty> getPropertiesByLandlordStatusNotAvailable(Long landlordId);

    PropertyCheckoutDTO getPropertyToCheckoutById(Long id);

    Long countTotalRentalProperty();

    Long countRevenueRentalProperty(Long landlordId);

    List<RentalProperty> findByLandlordId(Long landlordId);

    List<RentalProperty> findByLandlordIdWithBookings(Long landlordId);

    List<PropertyPerformanceDTO> getPropertyPerformanceForLandlord(Long landlordId);

    List<RecentInquiryDTO> getRecentInquiriesForLandlord(Long landlordId,
            int limit);
}
