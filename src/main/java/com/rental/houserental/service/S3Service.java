package com.rental.houserental.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {

    String uploadFile(String objectKey, String contentType, byte[] fileBytes);
    
    String uploadPropertyImage(MultipartFile file, Long propertyId);
    
    List<String> uploadPropertyImages(List<MultipartFile> files, Long propertyId);
    
    void deleteFile(String objectKey);
    
    String getFileUrl(String objectKey);
}
