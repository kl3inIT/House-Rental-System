package com.rental.houserental.service;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingService {

    void createListingWithPayment(CreateListingRequestDTO request, User landlord, ListingPriceDTO priceDTO);

    void deleteListing(Long listingId, User landlord);
    
    Page<ListingListItemDTO> getListingsForLandlord(ListingFilterDTO filter, Long landlordId, Pageable pageable);
    
    ListingStatsDTO getListingStats(Long landlordId, ListingFilterDTO filter);

    void hideListing(Long listingId, User landlord);

    void unhideListing(Long listingId, User landlord);

    ListingPriceDTO getListingPriceDTO();
} 