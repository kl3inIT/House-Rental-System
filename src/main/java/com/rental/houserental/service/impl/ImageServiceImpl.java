package com.rental.houserental.service.impl;

import com.rental.houserental.entity.PropertyImage;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.repository.ImageRepository;
import com.rental.houserental.service.ImageService;
import com.rental.houserental.service.S3Service;
import com.rental.houserental.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    @Override
    public List<PropertyImage> uploadPropertyImages(List<MultipartFile> files, RentalProperty property) {
        List<PropertyImage> uploadedImages = new ArrayList<>();
        
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                boolean isMainImage = i == 0; // First image is main by default
                PropertyImage image = uploadPropertyImage(file, property, isMainImage);
                uploadedImages.add(image);
            }
        }
        
        log.info("Uploaded {} images for property ID: {}", uploadedImages.size(), property.getId());
        return uploadedImages;
    }

    @Override
    public PropertyImage uploadPropertyImage(MultipartFile file, RentalProperty property, boolean isMainImage) {
        try {
            // If this is being set as main image, unset other main images
            if (isMainImage) {
                unsetMainImageForProperty(property.getId());
            }

            // Upload to S3
            String imageUrl = s3Service.uploadPropertyImage(file, property.getId());

            // Get next display order
            Integer nextOrder = getNextDisplayOrder(property.getId());

            // Create and save image entity
            PropertyImage image = PropertyImage.builder()
                    .imageUrl(imageUrl)
                    .fileName(file.getOriginalFilename())
                    .fileSize(file.getSize())
                    .isMainImage(isMainImage)
                    .displayOrder(nextOrder)
                    .rentalProperty(property)
                    .build();

            PropertyImage savedImage = imageRepository.save(image);
            log.info("Successfully uploaded image {} for property {}", savedImage.getId(), property.getId());
            
            return savedImage;
            
        } catch (Exception e) {
            log.error("Failed to upload image for property {}: {}", property.getId(), e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public void deleteImage(Long imageId) {
        PropertyImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        try {
            // Delete from S3
            String objectKey = ImageUtils.extractObjectKeyFromS3Url(image.getImageUrl());
            s3Service.deleteFile(objectKey);

            // Delete from database
            imageRepository.delete(image);
            
            log.info("Successfully deleted image with ID: {}", imageId);
            
        } catch (Exception e) {
            log.error("Failed to delete image {}: {}", imageId, e.getMessage());
            throw new RuntimeException("Failed to delete image", e);
        }
    }

    @Override
    public void setMainImage(Long imageId) {
        PropertyImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        // Unset other main images for this property
        unsetMainImageForProperty(image.getRentalProperty().getId());

        // Set this image as main
        image.setIsMainImage(true);
        imageRepository.save(image);
        
        log.info("Set image {} as main image for property {}", imageId, image.getRentalProperty().getId());
    }

    @Override
    public void updateImageOrder(Long imageId, Integer newOrder) {
        PropertyImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found with ID: " + imageId));

        image.setDisplayOrder(newOrder);
        imageRepository.save(image);
        
        log.info("Updated display order for image {} to {}", imageId, newOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyImage> getImagesByProperty(Long propertyId) {
        return imageRepository.findByRentalPropertyIdOrderByDisplayOrderAscCreatedAtAsc(propertyId);
    }

    private void unsetMainImageForProperty(Long propertyId) {
        List<PropertyImage> mainImages = imageRepository.findByRentalPropertyIdAndIsMainImageTrue(propertyId);
        mainImages.forEach(image -> image.setIsMainImage(false));
        imageRepository.saveAll(mainImages);
    }

    private Integer getNextDisplayOrder(Long propertyId) {
        Integer maxOrder = imageRepository.findMaxDisplayOrderByPropertyId(propertyId);
        return maxOrder != null ? maxOrder + 1 : 0;
    }


} 