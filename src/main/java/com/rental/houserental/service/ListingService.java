package com.rental.houserental.service;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.request.listing.UpdateListingRequestDTO;
import com.rental.houserental.dto.response.listing.ListingDetailDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ListingService {

    void createListing(CreateListingRequestDTO request, User landlord);
    
    void updateListing(Long listingId, UpdateListingRequestDTO request, User landlord);
    
    void deleteListing(Long listingId, User landlord);
    
    ListingDetailDTO getListingDetailById(Long listingId, User landlord);
    
    Page<ListingListItemDTO> getListingsForLandlord(ListingFilterDTO filter, Long landlordId, Pageable pageable);
    
    List<ListingListItemDTO> getListingsForProperty(Long propertyId, User landlord);
    
    ListingStatsDTO getListingStats(Long landlordId, ListingFilterDTO filter);

    void processPayment(User landlord, double price);

    double calculateListingFee(boolean isHighlight, String durationType, Integer duration, ListingPriceDTO listingPriceDTO);
} 