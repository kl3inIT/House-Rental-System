package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.request.property.SearchPropertyCriteriaDTO;
import com.rental.houserental.dto.response.property.FeaturedPropertyResponseDTO;
import com.rental.houserental.dto.response.property.SearchPropertyResponseDTO;
import com.rental.houserental.entity.PropertyImage;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.exceptions.property.ImageUploadException;
import com.rental.houserental.exceptions.property.PropertyNotFoundException;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.repository.specification.RentalPropertySpecification;

import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.ImageService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ImageService imageService;
    private final CategoryService categoryService;

    @Override
    public RentalProperty createProperty(CreatePropertyRequestDTO request, User landlord,
                                         MultipartFile[] imageFiles) {
        log.info("Creating property for landlord: {}", landlord.getEmail());

        try {
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
                    savedProperty = propertyRepository.save(savedProperty);
                    log.info("Uploaded {} images for property: {}", propertyImages.size(), savedProperty.getId());
                } catch (Exception e) {
                    log.error("Failed to upload images for property: {}", savedProperty.getId(), e);
                    // Delete the property since image upload failed
                    propertyRepository.delete(savedProperty);
                    throw new ImageUploadException("Failed to upload images for property. Property creation cancelled.", e);
                }
            }

            log.info("Successfully created property with ID: {}", savedProperty.getId());
            return savedProperty;
            
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
                    PropertyStatus.AVAILABLE, pageable);

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
            List<String> imageUrls = property.getImages().stream()
                    .map(PropertyImage::getImageUrl)
                    .toList();
            
            return FeaturedPropertyResponseDTO.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .price(property.getMonthlyRent())
                    .bedrooms(property.getBedrooms())
                    .bathrooms(property.getBathrooms())
                    .area(property.getArea())
                    .location(property.getWard() + ", " + property.getProvince())
                    .mainImageUrl(property.getMainImageUrl())
                    .imageUrls(imageUrls)
                    .imageCount(imageUrls.size())
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

}
