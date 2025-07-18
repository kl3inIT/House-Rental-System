package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.listing.CreateListingRequestDTO;
import com.rental.houserental.dto.request.listing.ListingFilterDTO;
import com.rental.houserental.dto.response.listing.ListingListItemDTO;
import com.rental.houserental.dto.response.listing.ListingPriceDTO;
import com.rental.houserental.dto.response.listing.ListingStatsDTO;
import com.rental.houserental.entity.*;
import com.rental.houserental.enums.ListingStatus;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.exceptions.listing.InsufficientBalanceException;
import com.rental.houserental.exceptions.listing.ListingNotFoundException;
import com.rental.houserental.exceptions.listing.UnauthorizedListingAccessException;
import com.rental.houserental.exceptions.common.BadRequestException;
import com.rental.houserental.exceptions.common.ResourceNotFoundException;
import com.rental.houserental.repository.ListingRepository;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.repository.TransactionRepository;
import com.rental.houserental.repository.UserRepository;
import com.rental.houserental.service.ListingService;
import com.rental.houserental.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SystemConfigService systemConfigService;

    private void createListing(CreateListingRequestDTO request, User landlord) {
        RentalProperty property = propertyRepository.findById(request.getRentalPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found"));

        if (!property.getLandlord().getId().equals(landlord.getId())) {
            throw new UnauthorizedListingAccessException("You can only create listings for your own properties");
        }

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
                .isHighlight(request.getIsHighlight())
                .rentalProperty(property)
                .landlord(landlord)
                .status(status)
                .build();
        listingRepository.save(listing);
    }

    private LocalDateTime calculateEndDate(LocalDateTime start, Integer duration, String durationType) {
        int dur = (duration == null || duration <= 0) ? 1 : duration;
        String durationTypeSelected = durationType != null ? durationType.trim() : "";
        return switch (durationTypeSelected) {
            case "MONTH" -> start.plusMonths(dur);
            case "WEEK"  -> start.plusWeeks(dur);
            default      -> throw new IllegalArgumentException("Invalid duration type: " + durationType);
        };
    }

    private double calculateListingFee(boolean isHighlight, String durationType, Integer duration, ListingPriceDTO priceDTO) {
        int dur = (duration == null || duration <= 0) ? 1 : duration;
        String durationTypeSelected = durationType != null ? durationType.trim() : "";
        double unitPrice = switch (durationTypeSelected) {
            case "MONTH" -> isHighlight ? priceDTO.getHighlightPricePerMonth().doubleValue() : priceDTO.getNormalPricePerMonth().doubleValue();
            case "WEEK"  -> isHighlight ? priceDTO.getHighlightPricePerWeek().doubleValue() : priceDTO.getNormalPricePerWeek().doubleValue();
            default      -> throw new IllegalArgumentException("Invalid duration type: " + durationType);
        };
        return unitPrice * dur;
    }

    private void processPayment(User landlord, double price) {
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

    @Override
    public void createListingWithPayment(CreateListingRequestDTO request, User landlord, ListingPriceDTO priceDTO) {
        double totalPrice = calculateListingFee(request.getIsHighlight(), request.getDurationType(), request.getDuration(), priceDTO);
        if (landlord.getBalance() < totalPrice) {
            throw new InsufficientBalanceException("You do not have enough balance to create this listing. Please top up your wallet to continue.");
        }
        processPayment(landlord, totalPrice);
        createListing(request, landlord);
    }

    private Listing getListingById(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> new ListingNotFoundException("Listing not found"));
    }

    @Override
    @Transactional
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
    public void hideListing(Long listingId, User landlord) {
        Listing listing = getListingById(listingId);
        if (!listing.getLandlord().getId().equals(landlord.getId())) {
            throw new AccessDeniedException("You do not have permission to hide this listing");
        }

        listing.setStatus(ListingStatus.HIDDEN);
        listingRepository.save(listing);
    }

    @Override
    public void unhideListing(Long listingId, User landlord) {
        Listing listing = getListingById(listingId);

        if (!listing.getLandlord().getId().equals(landlord.getId())) {
            throw new AccessDeniedException("You do not have permission to unhide this listing");
        }

        listing.setStatus(ListingStatus.ACTIVE);
        listingRepository.save(listing);
    }

    @Override
    public ListingPriceDTO getListingPriceDTO() {
        return ListingPriceDTO.builder()
                .normalPricePerMonth(systemConfigService.getNormalListingPricePerMonth())
                .highlightPricePerMonth(systemConfigService.getHighlightListingPricePerMonth())
                .normalPricePerWeek(systemConfigService.getNormalListingPricePerWeek())
                .highlightPricePerWeek(systemConfigService.getHighlightListingPricePerWeek())
                .build();
    }

    @Override
    @Transactional
    public Page<ListingListItemDTO> getListingsForLandlord(ListingFilterDTO filter, Long landlordId, Pageable pageable) {
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
    public ListingStatsDTO getListingStats(Long landlordId, ListingFilterDTO filter) {
        LocalDateTime now = LocalDateTime.now();
        
        long totalListings = listingRepository.countByLandlordId(landlordId);
        long activeListings = listingRepository.countByLandlordIdAndStartDateLessThanEqualAndEndDateGreaterThan(landlordId, now, now);
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

    private ListingListItemDTO convertToListItemDTO(Listing listing) {
        RentalProperty property = listing.getRentalProperty();
        ListingStatus status = computeListingStatus(listing);
        String mainImageUrl = getMainImageUrl(property);
        int daysLeft = computeDaysLeft(status.name(), listing);
        String propertyAddress = property.getStreetAddress() + ", " + property.getWard() + ", " + property.getProvince();

        return ListingListItemDTO.builder()
                .id(listing.getId())
                .startDate(listing.getStartDate())
                .endDate(listing.getEndDate())
                .amount(listing.getAmount())
                .description(listing.getDescription())
                .isHighlight(listing.isHighlight())
                .status(status.name())
                .propertyId(listing.getRentalProperty().getId())
                .propertyTitle(listing.getRentalProperty().getTitle())
                .propertyAddress(propertyAddress)
                .propertyMainImageUrl(mainImageUrl)
                .createdAt(listing.getCreatedAt())
                .daysLeft(daysLeft)
                .viewCount(listing.getRentalProperty().getViews())
                .inquiryCount(0)
                .build();
    }

    private String getMainImageUrl(RentalProperty property) {
        Set<PropertyImage> images = Optional.ofNullable(property.getImages()).orElse(Set.of());

        return images.stream()
                .filter(PropertyImage::getIsMainImage)
                .findFirst()
                .or(() -> images.stream().findFirst())
                .map(PropertyImage::getImageUrl)
                .orElse(null);
    }

    public ListingStatus computeListingStatus(Listing listing) {
        LocalDateTime now = LocalDateTime.now();
        if (listing.getEndDate().isBefore(now)) return ListingStatus.EXPIRED;
        if (listing.getStartDate().isAfter(now)) return ListingStatus.UPCOMING;
        if (listing.getStatus() == ListingStatus.HIDDEN) return ListingStatus.HIDDEN;
        return ListingStatus.ACTIVE;
    }

    private int computeDaysLeft(String status, Listing listing) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = "UPCOMING".equals(status)
                ? listing.getStartDate().toLocalDate()
                : listing.getEndDate().toLocalDate();
        return (int) (targetDate.toEpochDay() - now.toEpochDay());
    }

} 