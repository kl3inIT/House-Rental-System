package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.dto.response.FeaturedPropertyResponseDTO;
import com.rental.houserental.entity.PropertyImage;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.exceptions.property.ImageUploadException;
import com.rental.houserental.repository.PropertyRepository;

import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.ImageService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        RentalProperty property = RentalProperty.builder()
                .title(request.getTitle())
                .category(categoryService.findById(request.getCategoryId()))
                .monthlyRent(request.getMonthlyRent())
                .bedrooms(request.getBedrooms())
                .bathrooms(request.getBathrooms())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
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
    }

    @Override
    public List<FeaturedPropertyResponseDTO> getFeaturedProperties(int limit) {
        log.info("Fetching {} featured properties", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<RentalProperty> properties = propertyRepository.findFeaturedProperties(
                PropertyStatus.AVAILABLE, pageable);

        List<FeaturedPropertyResponseDTO> result = properties.stream()
                .map(this::convertToFeaturedDTO)
                .toList();

        log.info("Found {} featured properties", result.size());
        return result;
    }

    private FeaturedPropertyResponseDTO convertToFeaturedDTO(RentalProperty property) {
        return FeaturedPropertyResponseDTO.builder()
                .id(property.getId())
                .title(property.getTitle())
                .price(property.getMonthlyRent())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .location(property.getCity() + ", " + property.getProvince())
                .imageUrl(property.getMainImageUrl())
                .build();
    }
}
