package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.CreatePropertyRequest;
import com.rental.houserental.entity.PropertyImage;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.PropertyStatus;
import com.rental.houserental.repository.PropertyRepository;

import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.ImageService;
import com.rental.houserental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public RentalProperty createProperty(CreatePropertyRequest request, User landlord, 
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
                .securityDeposit(request.getSecurityDeposit())
                .minLeaseDuration(request.getMinLeaseDuration())
                .propertyStatus(PropertyStatus.DRAFT)
                .landlord(landlord)
                .images(new HashSet<>())
                .build();

        RentalProperty savedProperty = propertyRepository.save(property);

        // Handle image uploads
        if (imageFiles != null && imageFiles.length > 0) {
            try {
                List<MultipartFile> fileList = Arrays.asList(imageFiles);
                List<PropertyImage> propertyImages = imageService.uploadPropertyImages(fileList, savedProperty);
                savedProperty.setImages(new HashSet<>(propertyImages));
                savedProperty = propertyRepository.save(savedProperty);
                log.info("Uploaded {} images for property: {}", propertyImages.size(), savedProperty.getId());
            } catch (Exception e) {
                log.error("Failed to upload images for property: {}", savedProperty.getId(), e);
            }
        }

        log.info("Successfully created property with ID: {}", savedProperty.getId());
        return savedProperty;
    }
}
