package com.rental.houserental.service;

import com.rental.houserental.entity.PropertyImage;
import com.rental.houserental.entity.RentalProperty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    
    List<PropertyImage> uploadPropertyImages(List<MultipartFile> files, RentalProperty property);

    PropertyImage uploadPropertyImage(MultipartFile file, RentalProperty property, boolean isMainImage);
    
    void deleteImage(Long imageId);
    
    void setMainImage(Long imageId);
    
    void updateImageOrder(Long imageId, Integer newOrder);
    
    List<PropertyImage> getImagesByProperty(Long propertyId);
} 