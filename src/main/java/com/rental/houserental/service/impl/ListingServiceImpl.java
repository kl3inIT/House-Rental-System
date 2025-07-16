package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.request.listing.UpdateListingRequestDTO;
import com.rental.houserental.dto.response.listing.ListingDetailDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.*;
import com.rental.houserental.enums.ListingStatus;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.exceptions.listing.ListingNotFoundException;
import com.rental.houserental.exceptions.listing.UnauthorizedListingAccessException;
import com.rental.houserental.exceptions.common.BadRequestException;
import com.rental.houserental.exceptions.common.ResourceNotFoundException;
import com.rental.houserental.repository.ListingRepository;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.repository.TransactionRepository;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void createListing(CreateListingRequestDTO request, User landlord) {
        RentalProperty property = propertyRepository.findById(request.getRentalPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new UnauthorizedListingAccessException("You can only create listings for your own properties");
        }

        property.setPropertyStatus(PropertyStatus.AVAILABLE);
        propertyRepository.save(property);

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();
        LocalDateTime endDateTime = calculateEndDate(startDateTime, request.getDuration(), request.getDurationType());

        ListingStatus status;
        LocalDate today = LocalDate.now();
        if (!startDateTime.toLocalDate().isAfter(today)) {
            status = ListingStatus.ACTIVE;
        } else {
            status = ListingStatus.UPCOMING;
        }

        Listing listing = Listing.builder()
                .startDate(startDateTime)
                .endDate(endDateTime)
                .amount(property.getMonthlyRent().doubleValue())
                .description(request.getDescription())
                .isHighlight(Boolean.TRUE.equals(request.getIsHighlight()))
                .rentalProperty(property)
                .landlord(landlord)
                .status(status)
                .build();

        listingRepository.save(listing);
    }

    public double calculateListingFee(boolean isHighlight, String durationType, Integer duration, ListingPriceDTO priceDTO) {
        int dur = (duration == null || duration <= 0) ? 1 : duration;
        double unitPrice = switch (durationType != null ? durationType.toUpperCase() : "") {
            case "MONTH" -> isHighlight ? priceDTO.getHighlightPricePerMonth().doubleValue() : priceDTO.getNormalPricePerMonth().doubleValue();
            case "WEEK"  -> isHighlight ? priceDTO.getHighlightPricePerWeek().doubleValue() : priceDTO.getNormalPricePerWeek().doubleValue();
            default      -> throw new IllegalArgumentException("Invalid duration type: " + durationType);
        };
        return unitPrice * dur;
    }

    public void processPayment(User landlord, double price) {
        landlord.setBalance(landlord.getBalance() - price);
        userRepository.save(landlord);

        Transaction transaction = Transaction.builder()
                .amount(price)
                .type(TransactionType.PAYMENT)
                .description("Listing payment")
                .balanceAfter(landlord.getBalance())
                .user(landlord)
                .build();
        transactionRepository.save(transaction);
    }

    private LocalDateTime calculateEndDate(LocalDateTime start, Integer duration, String durationType) {
        int dur = (duration == null || duration <= 0) ? 1 : duration;
        return switch (durationType != null ? durationType.toUpperCase() : "") {
            case "MONTH" -> start.plusMonths(dur);
            case "WEEK"  -> start.plusWeeks(dur);
            default      -> throw new IllegalArgumentException("Invalid duration type: " + durationType);
        };
    }

    @Override
    public void updateListing(Long listingId, UpdateListingRequestDTO request, User landlord) {
        Listing listing = getListingById(listingId);

        if (!listing.getLandlord().getId().equals(landlord.getId())) {
            throw new UnauthorizedListingAccessException("You can only update your own listings");
        }

        // Validate date range
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        listing.setStartDate(request.getStartDate());
        listing.setEndDate(request.getEndDate());
        listing.setAmount(request.getAmount());
        listing.setDescription(request.getDescription());
        listing.setHighlight(request.getIsHighlight());

        listingRepository.save(listing);
    }

    private Listing getListingById(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));
    }

    @Override
    public void deleteListing(Long listingId, User landlord) {
        Listing listing = getListingById(listingId);

        if (!listing.getLandlord().getId().equals(landlord.getId())) {
            throw new UnauthorizedListingAccessException("You can only delete your own listings");
        }

        listingRepository.delete(listing);
        RentalProperty property = listing.getRentalProperty();
        property.setPropertyStatus(PropertyStatus.DRAFT);
    }

    @Override
    @Transactional(readOnly = true)
    public ListingDetailDTO getListingDetailById(Long listingId, User landlord) {
        Listing listing = getListingById(listingId);

        if (!listing.getLandlord().getId().equals(landlord.getId())) {
            throw new UnauthorizedListingAccessException("You can only view your own listings");
        }

        return convertToDetailDTO(listing);
    }

    @Override
    @Transactional
    public Page<ListingListItemDTO> getListingsForLandlord(ListingFilterDTO filter, Long landlordId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        List<Listing> expiredListings = listingRepository.findByLandlordIdAndEndDateBeforeAndStatusNot(
                landlordId, now, ListingStatus.EXPIRED);
        if (!expiredListings.isEmpty()) {
            for (Listing l : expiredListings) {
                l.setStatus(ListingStatus.EXPIRED);
            }
            listingRepository.saveAll(expiredListings);
        }

        String searchTerm = (filter.getSearchTerm() != null && !filter.getSearchTerm().isBlank()) ? filter.getSearchTerm().trim() : null;
        Boolean isHighlight = null;
        if ("highlighted".equalsIgnoreCase(filter.getHighlight())) isHighlight = true;
        else if ("regular".equalsIgnoreCase(filter.getHighlight())) isHighlight = false;

        ListingStatus status = getListingStatus(filter);

        Long propertyId = filter.getPropertyId();

        Page<Listing> listings = listingRepository.searchListingsForLandlord(
                landlordId, searchTerm, isHighlight, status, propertyId, pageable
        );
        return listings.map(this::convertToListItemDTO);
    }

    private static ListingStatus getListingStatus(ListingFilterDTO filter) {
        String statusStr = filter.getStatus();
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return null;
        }
        statusStr = statusStr.trim().toUpperCase();
        return switch (statusStr) {
            case "ACTIVE" -> ListingStatus.ACTIVE;
            case "EXPIRED" -> ListingStatus.EXPIRED;
            case "UPCOMING" -> ListingStatus.UPCOMING;
            default -> throw new BadRequestException("Invalid listing status: " + statusStr);
        };
    }



    @Override
    @Transactional(readOnly = true)
    public List<ListingListItemDTO> getListingsForProperty(Long propertyId, User landlord) {
        List<Listing> listings = listingRepository.findByRentalPropertyIdAndLandlordId(propertyId, landlord.getId());
        return listings.stream()
                .map(this::convertToListItemDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ListingStatsDTO getListingStats(Long landlordId, ListingFilterDTO filter) {
        LocalDateTime now = LocalDateTime.now();
        
        long totalListings = listingRepository.countByLandlordId(landlordId);
        long activeListings = listingRepository.countByLandlordIdAndStartDateLessThanEqualAndEndDateGreaterThan(
                landlordId, now, now);
        long expiredListings = listingRepository.countByLandlordIdAndEndDateLessThan(landlordId, now);
        long upcomingListings = listingRepository.countByLandlordIdAndStartDateGreaterThan(landlordId, now);
        long highlightedListings = listingRepository.countByLandlordIdAndIsHighlightTrue(landlordId);

        long totalViews = 0;
        if (filter.getPropertyId() != null) {
            RentalProperty property = propertyRepository.findById(filter.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Property not found"));
            totalViews = property.getViews();
        } else {
            List<RentalProperty> properties = propertyRepository.findByLandlordId(landlordId);
            totalViews = properties.stream().mapToLong(RentalProperty::getViews).sum();
        }
        long totalInquiries = 0L;
        double totalRevenue = 0.0;

        return ListingStatsDTO.builder()
                .totalListings(totalListings)
                .activeListings(activeListings)
                .expiredListings(expiredListings)
                .upcomingListings(upcomingListings)
                .highlightedListings(highlightedListings)
                .totalViews(totalViews)
                .totalInquiries(totalInquiries)
                .totalRevenue(totalRevenue)
                .build();
    }

    private ListingDetailDTO convertToDetailDTO(Listing listing) {
        return ListingDetailDTO.builder()
                .id(listing.getId())
                .startDate(listing.getStartDate())
                .endDate(listing.getEndDate())
                .amount(listing.getAmount())
                .description(listing.getDescription())
                .isHighlight(listing.isHighlight())
                .status(computeListingStatus(listing).name())
                .propertyId(listing.getRentalProperty().getId())
                .propertyTitle(listing.getRentalProperty().getTitle())
                .propertyAddress(listing.getRentalProperty().getStreetAddress())
                .propertyImageUrl(listing.getRentalProperty().getImages() != null &&
                        !listing.getRentalProperty().getImages().isEmpty() ?
                        listing.getRentalProperty().getImages().iterator().next().getImageUrl() : null)
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .viewCount(0) // Placeholder - would come from analytics
                .inquiryCount(0) // Placeholder - would come from analytics
                .build();
    }

    private ListingListItemDTO convertToListItemDTO(Listing listing) {
        return ListingListItemDTO.builder()
                .id(listing.getId())
                .startDate(listing.getStartDate())
                .endDate(listing.getEndDate())
                .amount(listing.getAmount())
                .description(listing.getDescription())
                .isHighlight(listing.isHighlight())
                .status(computeListingStatus(listing).name())
                .propertyId(listing.getRentalProperty().getId())
                .propertyTitle(listing.getRentalProperty().getTitle())
                .propertyAddress(listing.getRentalProperty().getStreetAddress())
                .propertyMainImageUrl(
                        listing.getRentalProperty().getImages().stream()
                                .filter(PropertyImage::getIsMainImage)
                                .map(PropertyImage::getImageUrl)
                                .findFirst()
                                .orElse(
                                        listing.getRentalProperty().getImages().isEmpty()
                                                ? null
                                                : listing.getRentalProperty().getImages().iterator().next().getImageUrl()
                                )
                )
                .createdAt(listing.getCreatedAt())
                .viewCount(listing.getRentalProperty().getViews())
                .inquiryCount(0)
                .build();
    }

    public ListingStatus computeListingStatus(Listing listing) {
        LocalDateTime now = LocalDateTime.now();
        if (listing.getEndDate().isBefore(now)) return ListingStatus.EXPIRED;
        if (listing.getStartDate().isAfter(now)) return ListingStatus.UPCOMING;
        return ListingStatus.ACTIVE;
    }
} 