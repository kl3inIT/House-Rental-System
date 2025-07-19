package com.rental.houserental.service.impl;

import com.rental.houserental.dto.dashboard.RecentInquiryDTO;
import com.rental.houserental.dto.request.property.*;
import com.rental.houserental.dto.response.property.*;
import com.rental.houserental.entity.*;
import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.enums.TransactionType;
import com.rental.houserental.exceptions.booking.InvalidBookingStatusException;
import com.rental.houserental.exceptions.common.ResourceNotFoundException;
import com.rental.houserental.exceptions.property.ImageUploadException;
import com.rental.houserental.exceptions.property.PropertyDeleteException;
import com.rental.houserental.exceptions.property.PropertyNotFoundException;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.exceptions.user.UserNotFoundException;
import com.rental.houserental.repository.*;
import com.rental.houserental.repository.specification.RentalPropertySpecification;
import com.rental.houserental.dto.dashboard.PropertyPerformanceDTO;
import com.rental.houserental.entity.Booking;
import com.rental.houserental.entity.Review;
import com.rental.houserental.enums.BookingStatus;
import com.rental.houserental.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.rental.houserental.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.temporal.ChronoUnit;

import static com.rental.houserental.constant.ErrorMessageConstant.MSG_400;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final AmenityService amenityService;
    private final S3Service s3Service;
    private final UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public void createProperty(CreatePropertyRequestDTO request, User landlord,
            MultipartFile[] imageFiles) {
        try {
            Set<Amenity> amenities = new HashSet<>();
            if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
                amenities = new HashSet<>(amenityService.findAllByIds(request.getAmenityIds()));
            }
            RentalProperty property = RentalProperty.builder()
                    .title(request.getTitle())
                    .category(categoryService.findById(request.getCategoryId()))
                    .monthlyRent(request.getMonthlyRent())
                    .bedrooms(request.getBedrooms())
                    .bathrooms(request.getBathrooms())
                    .area(request.getArea())
                    .streetAddress(request.getStreetAddress())
                    .ward(request.getWard())
                    .province(request.getProvince())
                    .description(request.getDescription())
                    .propertyStatus(PropertyStatus.DRAFT)
                    .landlord(landlord)
                    .furnishing(request.getFurnishing() != null ? FurnishingType.fromString(request.getFurnishing())
                            : FurnishingType.NONE)
                    .depositPercentage(request.getDepositPercentage())
                    .amenities(amenities)
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .views(0)
                    .images(new HashSet<>())
                    .build();

            RentalProperty savedProperty = propertyRepository.save(property);

            if (imageFiles != null && imageFiles.length > 0) {
                try {
                    List<MultipartFile> fileList = Arrays.asList(imageFiles);
                    List<PropertyImage> propertyImages = imageService.uploadPropertyImages(fileList, savedProperty);
                    for (PropertyImage image : propertyImages) {
                        image.setRentalProperty(savedProperty);
                    }
                    savedProperty.getImages().addAll(propertyImages);
                } catch (Exception e) {
                    // Delete the property since image upload failed
                    propertyRepository.delete(savedProperty);
                    throw new ImageUploadException("Failed to upload images for property. Property creation cancelled.",
                            e);
                }
            }

        } catch (Exception e) {
            log.error("Failed to create property for landlord: {}", landlord.getEmail(), e);
            throw e;
        }
    }

    @Override
    public List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit) {
        log.info("Fetching {} featured properties", limit);

        try {
            Pageable pageable = PageRequest.of(0, limit);
            List<RentalProperty> properties = propertyRepository.findFeaturedProperties(
                    List.of(PropertyStatus.AVAILABLE, PropertyStatus.BOOKED, PropertyStatus.RENTED), pageable);

            List<FeaturedPropertyResponseDTO> result = properties.stream()
                    .map(this::convertToFeaturedDTO)
                    .toList();

            log.info("Found {} featured properties", result.size());
            return result;

        } catch (Exception e) {
            log.error("Error fetching featured properties", e);
            return List.of();
        }
    }

    @Override
    public Page<SearchPropertyResponseDTO> searchProperties(SearchPropertyCriteriaDTO criteria, Pageable pageable) {
        Specification<RentalProperty> spec = RentalPropertySpecification.withCriteria(criteria);
        Page<RentalProperty> propertyPage = propertyRepository.findAll(spec, pageable);
        List<SearchPropertyResponseDTO> dtoList = propertyPage.getContent().stream()
                .map(this::convertToSearchDTO)
                .toList();
        return new PageImpl<>(dtoList, pageable, propertyPage.getTotalElements());
    }

    @Override
    public Page<PropertyListItemDTO> searchPropertiesForLandlord(
            LandlordPropertyFilterDTO filter, Long landlordId, Pageable pageable) {

        Page<RentalProperty> page = propertyRepository.searchByLandlord(
                landlordId,
                filter.getStatus(),
                filter.getKeyword(),
                filter.getMinPrice() != null ? filter.getMinPrice() : null,
                filter.getMaxPrice() != null ? filter.getMaxPrice() : null,
                pageable);
        return page.map(this::toPropertyListItemDTO);
    }

    @Override
    public PropertyStatsDTO getPropertyStats(Long landlordId, LandlordPropertyFilterDTO filter) {
        List<Object[]> results = propertyRepository.getStatsByLandlord(
                landlordId,
                filter.getStatus(),
                filter.getKeyword(),
                filter.getMinPrice(),
                filter.getMaxPrice());

        Object[] arr = (results != null && !results.isEmpty()) ? results.getFirst() : new Object[11];

        return PropertyStatsDTO.builder()
                .totalProperties(arr[0] == null ? 0L : ((Number) arr[0]).longValue())
                .availableCount(arr[1] == null ? 0L : ((Number) arr[1]).longValue())
                .rentedCount(arr[2] == null ? 0L : ((Number) arr[2]).longValue())
                .bookedCount(arr[3] == null ? 0L : ((Number) arr[3]).longValue())
                .draftCount(arr[4] == null ? 0L : ((Number) arr[4]).longValue())
                .unavailableCount(arr[5] == null ? 0L : ((Number) arr[5]).longValue())
                .expiredCount(arr[6] == null ? 0L : ((Number) arr[6]).longValue())
                .adminHiddenCount(arr[7] == null ? 0L : ((Number) arr[7]).longValue())
                .adminBannedCount(arr[8] == null ? 0L : ((Number) arr[8]).longValue())
                .totalViews(arr[9] == null ? 0 : ((Number) arr[9]).intValue())
                .totalRevenue(arr[10] == null ? BigDecimal.ZERO : (BigDecimal) arr[10])
                .build();
    }

    @Override
    public PropertyDetailDTO getPropertyDetailById(Long id) {
        RentalProperty property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));
        List<AmenityDTO> amenityDTOs = property.getAmenities().stream()
                .sorted(Comparator.comparing(Amenity::getId))
                .map(amenity -> AmenityDTO.builder()
                        .id(amenity.getId())
                        .name(amenity.getName())
                        .description(amenity.getDescription())
                        .icon(amenity.getIcon())
                        .build())
                .toList();
        return PropertyDetailDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .categoryId(property.getCategory() != null ? property.getCategory().getId() : null)
                .monthlyRent(property.getMonthlyRent())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .area(property.getArea())
                .streetAddress(property.getStreetAddress())
                .ward(property.getWard())
                .province(property.getProvince())
                .fullAddress(property.getStreetAddress() + ", " + property.getWard() + ", " + property.getProvince())
                .description(property.getDescription())
                .propertyStatus(property.getPropertyStatus().toString())
                .furnishing(property.getFurnishing() != null ? property.getFurnishing() : FurnishingType.FULL)
                .depositPercentage(property.getDepositPercentage())
                .amenities(amenityDTOs)
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .mainImageUrl(property.getMainImageUrl())
                .imageUrls(property.getImages().stream()
                        .map(PropertyImage::getImageUrl)
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public void updateProperty(Long id, UpdatePropertyRequestDTO dto, MultipartFile[] imageFiles) {
        RentalProperty property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));

        if (dto.getAmenityIds() != null) {
            Set<Amenity> amenities = new HashSet<>(amenityService.findAllByIds(dto.getAmenityIds()));
            property.setAmenities(amenities);
        }
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setMonthlyRent(dto.getMonthlyRent());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setArea(dto.getArea());
        property.setStreetAddress(dto.getStreetAddress());
        property.setWard(dto.getWard());
        property.setProvince(dto.getProvince());
        property.setDepositPercentage(dto.getDepositPercentage());
        property.setLatitude(dto.getLatitude());
        property.setLongitude(dto.getLongitude());

        List<String> imageUrlsToKeep = dto.getImageUrls() != null
                ? dto.getImageUrls().stream().filter(s -> s != null && !s.trim().isEmpty()).toList()
                : new ArrayList<>();

        Set<PropertyImage> currentImages = property.getImages();
        // Xóa các ảnh không còn giữ lại
        currentImages.removeIf(img -> !imageUrlsToKeep.contains(img.getImageUrl()));

        // Đánh lại thứ tự và isMainImage cho ảnh cũ
        int displayOrder = 0;
        for (String url : imageUrlsToKeep) {
            for (PropertyImage img : currentImages) {
                if (img.getImageUrl().equals(url)) {
                    img.setDisplayOrder(displayOrder);
                    img.setIsMainImage(displayOrder == 0);
                    displayOrder++;
                }
            }
        }

        // === Xử lý thêm các ảnh mới (nếu có upload hợp lệ) ===
        List<MultipartFile> validFiles = new ArrayList<>();
        if (imageFiles != null) {
            for (MultipartFile f : imageFiles) {
                if (f != null && !f.isEmpty() && f.getOriginalFilename() != null
                        && !f.getOriginalFilename().isBlank()) {
                    validFiles.add(f);
                }
            }
        }

        if (!validFiles.isEmpty()) {
            List<PropertyImage> newImages = imageService.uploadPropertyImages(validFiles, property);
            for (PropertyImage img : newImages) {
                img.setDisplayOrder(displayOrder);
                img.setIsMainImage(displayOrder == 0 && imageUrlsToKeep.isEmpty());
                displayOrder++;
                property.getImages().add(img);
            }
        }

        propertyRepository.save(property);
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        RentalProperty property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + id));

        if (property.getPropertyStatus() != PropertyStatus.DRAFT) {
            throw new PropertyDeleteException("");
        }
        if (!property.getBookings().isEmpty()) {
            throw new PropertyDeleteException("");
        }

        // Xóa toàn bộ ảnh trên S3 (theo đúng objectKey đã upload)
        for (PropertyImage image : property.getImages()) {
            String imageUrl = image.getImageUrl();
            try {
                String objectKey = extractObjectKeyFromUrl(imageUrl);
                s3Service.deleteFile(objectKey);
            } catch (Exception ex) {
                log.warn("Không xóa được ảnh S3: {}. Lỗi: {}", imageUrl, ex.getMessage());
            }
        }

        // Xóa property trong database
        propertyRepository.deleteById(id);
    }

    // Hàm này cắt objectKey giống lúc upload
    private String extractObjectKeyFromUrl(String imageUrl) {
        int idx = imageUrl.indexOf(".amazonaws.com/");
        if (idx != -1) {
            return imageUrl.substring(idx + ".amazonaws.com/".length());
        }
        throw new IllegalArgumentException("Không thể xác định object key từ url: " + imageUrl);
    }

    @Override
    public List<PropertyDetailDTO> getSimularProperties(Integer limit, Long propertyId) {
        PropertyDetailDTO currentProperty = getPropertyDetailById(propertyId);
        Long categoryId = currentProperty.getCategoryId();

        List<RentalProperty> properties = propertyRepository
                .findByCategoryIdAndIdNotAndPropertyStatus(
                        categoryId,
                        propertyId,
                        PropertyStatus.AVAILABLE,
                        PageRequest.of(0, limit));

        return properties.stream()
                .map(property -> PropertyDetailDTO.builder()
                        .id(property.getId())
                        .title(property.getTitle())
                        .categoryId(property.getCategory() != null ? property.getCategory().getId() : null)
                        // .categoryName(property.getCategory() != null ?
                        // property.getCategory().getName() : null)
                        .monthlyRent(property.getMonthlyRent())
                        .bedrooms(property.getBedrooms())
                        .bathrooms(property.getBathrooms())
                        .area(property.getArea())
                        .streetAddress(property.getStreetAddress())
                        .ward(property.getWard())
                        .province(property.getProvince())
                        .fullAddress(
                                property.getStreetAddress() + ", " + property.getWard() + ", " + property.getProvince())
                        .description(property.getDescription())
                        .propertyStatus(property.getPropertyStatus().toString())
                        .furnishing(property.getFurnishing())
                        .mainImageUrl(property.getMainImageUrl())
                        .imageUrls(
                                property.getImages().stream().map(PropertyImage::getImageUrl).toList())
                        .latitude(property.getLatitude())
                        .longitude(property.getLongitude())
                        .build())
                .toList();

    }

    @Override
    public PropertyCheckoutDTO getPropertyToCheckoutById(Long id) {
        RentalProperty property = propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));
        User currentUser = getCurrentUser();
        User landlord = property.getLandlord();
        return PropertyCheckoutDTO.builder()
                .id(property.getId())
                .name(property.getTitle())
                .address(property.getStreetAddress() + ", " + property.getWard() + ", " + property.getProvince())
                .image(property.getMainImageUrl())
                .renterName(currentUser.getName())
                .renterEmail(currentUser.getEmail())
                .renterPhone(currentUser.getPhone())
                .landlordName(landlord.getName())
                .landlordEmail(landlord.getEmail())
                .landlordPhone(landlord.getPhone())
                .price(property.getMonthlyRent().doubleValue())
                .percentageDeposit(property.getDepositPercentage().longValue())
                .renterBalance(currentUser.getBalance())
                .build();
    }

    @Override
    public Long countTotalRentalProperty() {
        return propertyRepository.count();
    }

    @Override
    public Long countRevenueRentalProperty(Long landlordId) {
        return propertyRepository.countRentedProperties();
    }

    public RentalProperty findPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found", MSG_400));
    }

    private PropertyListItemDTO toPropertyListItemDTO(RentalProperty p) {
        return PropertyListItemDTO.builder()
                .id(p.getId())
                .title(p.getTitle())
                .propertyStatus(p.getPropertyStatus())
                .monthlyRent(p.getMonthlyRent())
                .views(p.getViews())
                .createAt(p.getCreatedAt())
                .fullAddress(p.getStreetAddress() + ", " + p.getWard() + ", " + p.getProvince())
                .bedrooms(p.getBedrooms())
                .bathrooms(p.getBathrooms())
                .updateAt(p.getUpdatedAt())
                .listingCount(p.getListings().size())
                .bookingCount(p.getBookings().size())
                .area(p.getArea())
                .mainImageUrl(p.getMainImageUrl() != null ? p.getMainImageUrl() : "")
                .bookingCount(p.getBookings().size())
                .listingCount(p.getListings().size())
                .updateAt(p.getUpdatedAt())
                .build();
    }

    @Override
    public SearchPropertyResponseDTO getPropertyById(Long id) {
        log.info("Fetching property with ID: {}", id);

        try {
            RentalProperty property = propertyRepository.findById(id)
                    .orElseThrow(() -> new PropertyNotFoundException("Property not found with ID: " + id));

            SearchPropertyResponseDTO result = convertToSearchDTO(property);
            log.info("Found property: {}", result.getTitle());

            return result;

        } catch (PropertyNotFoundException e) {
            log.warn("Property not found with ID: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching property with ID: {}", id, e);
            throw new PropertyNotFoundException("Error retrieving property with ID: " + id, e);
        }
    }

    private FeaturedPropertyResponseDTO convertToFeaturedDTO(RentalProperty property) {
        try {
            log.debug("Converting property {} to featured DTO", property.getId());
            log.debug("Property title: {}", property.getTitle());
            log.debug("Property images count: {}", property.getImages() != null ? property.getImages().size() : 0);

            String mainImageUrl = property.getMainImageUrl();
            log.debug("Main image URL: {}", mainImageUrl);

            // Get top 3-4 amenities for display
            List<String> topAmenities = property.getAmenities().stream()
                    .limit(4)
                    .map(amenity -> amenity.getName())
                    .toList();

            return FeaturedPropertyResponseDTO.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .price(property.getMonthlyRent())
                    .bedrooms(property.getBedrooms())
                    .bathrooms(property.getBathrooms())
                    .area(property.getArea())
                    .location(property.getWard() + ", " + property.getProvince())
                    .fullAddress(property.getFullAddress())
                    .categoryName(property.getCategory() != null ? property.getCategory().getName() : null)
                    .furnishing(property.getFurnishing())
                    .depositPercentage(property.getDepositPercentage())
                    .views(property.getViews())
                    .mainImageUrl(mainImageUrl)
                    .topAmenities(topAmenities)
                    .rating(4.5)
                    .reviewCount(12)
                    .build();

        } catch (Exception e) {
            log.error("Error converting property to featured DTO: {}", property.getId(), e);
            throw e;
        }
    }

    private SearchPropertyResponseDTO convertToSearchDTO(RentalProperty property) {
        try {
            List<String> imageUrls = property.getImages().stream()
                    .map(PropertyImage::getImageUrl)
                    .toList();

            return SearchPropertyResponseDTO.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .monthlyRent(property.getMonthlyRent())
                    .bedrooms(property.getBedrooms())
                    .bathrooms(property.getBathrooms())
                    .area(property.getArea())
                    .streetAddress(property.getStreetAddress())
                    .ward(property.getWard())
                    .province(property.getProvince())
                    .description(property.getDescription())
                    .propertyStatus(property.getPropertyStatus().name())
                    .categoryName(property.getCategory().getName())
                    .mainImageUrl(property.getMainImageUrl())
                    .imageUrls(imageUrls)
                    .imageCount(imageUrls.size())
                    .publishedAt(property.getPublishedAt())
                    .build();

        } catch (Exception e) {
            log.error("Error converting property to search DTO: {}", property.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void increaseView(Long propertyId) {
        RentalProperty property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id " + propertyId));
        property.setViews(property.getViews() + 1);
        propertyRepository.save(property);
    }

    @Override
    public List<RentalProperty> getPropertiesByLandlordStatusNotAvailable(Long landlordId) {
        return propertyRepository.findByLandlordIdAndPropertyStatusNot(landlordId, PropertyStatus.AVAILABLE);
    }

    @Override
    public List<RentalProperty> findByLandlordId(Long landlordId) {
        return propertyRepository.findByLandlordId(landlordId);
    }

    @Override
    public List<RentalProperty> findByLandlordIdWithBookings(Long landlordId) {
        return propertyRepository.findByLandlordIdWithBookings(landlordId);
    }

    @Override
    public List<PropertyPerformanceDTO> getPropertyPerformanceForLandlord(Long landlordId) {
        List<RentalProperty> properties = findByLandlordId(landlordId);
        return properties.stream().map(property -> {
            List<Booking> bookings = bookingRepository.findByRentalPropertyId(property.getId());
            int inquiries = 0;
            for (Booking booking : bookings) {
                if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.ACTIVE
                        || booking.getStatus() == BookingStatus.COMPLETED) {
                    inquiries++;
                }
            }
            int views = property.getViews() != null ? property.getViews() : 0;
            return new PropertyPerformanceDTO(
                    property.getId(),
                    property.getTitle(),
                    property.getFullAddress(),
                    property.getMonthlyRent(),
                    views,
                    inquiries);
        }).collect(toList());
    }

    @Override
    public List<RecentInquiryDTO> getRecentInquiriesForLandlord(
            Long landlordId, int limit) {
        List<Booking> bookings = bookingRepository.findRecentByLandlordId(landlordId,
                PageRequest.of(0, limit));
        return bookings.stream().map(b -> new RecentInquiryDTO(
                b.getUser().getName(),
                b.getRentalProperty().getTitle(),
                b.getCreatedAt(),
                b.getStatus().name())).toList();
    }

}
