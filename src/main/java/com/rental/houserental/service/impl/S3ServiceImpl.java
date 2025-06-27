package com.rental.houserental.service.impl;

import com.rental.houserental.service.S3Service;
import com.rental.houserental.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${spring.aws.s3.bucket}")
    private String bucketName;

    @Value("${spring.aws.region}")
    private String region;

    @Override
    public String uploadFile(String objectKey, String contentType, byte[] fileBytes) {
        log.debug("Uploading file to S3 with key: {}", objectKey);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));
        log.info("Successfully uploaded file: {}", objectKey);
        return getFileUrl(objectKey);
    }

    @Override
    public String uploadPropertyImage(MultipartFile file, Long propertyId) {
        ImageUtils.validateImageFile(file);
        
        try {
            String fileName = ImageUtils.generateUniqueFileName(file.getOriginalFilename());
            String objectKey = String.format("properties/%d/%s", propertyId, fileName);
            
            return uploadFile(objectKey, file.getContentType(), file.getBytes());
        } catch (IOException e) {
            log.error("Failed to upload property image for property {}: {}", propertyId, e.getMessage());
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    public List<String> uploadPropertyImages(List<MultipartFile> files, Long propertyId) {
        List<String> uploadedUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String url = uploadPropertyImage(file, propertyId);
                uploadedUrls.add(url);
            }
        }
        
        log.info("Uploaded {} images for property {}", uploadedUrls.size(), propertyId);
        return uploadedUrls;
    }



    @Override
    public void deleteFile(String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file: {}", objectKey);
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", objectKey, e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public String getFileUrl(String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, objectKey);
    }

    private String extractObjectKeyFromUrl(String url) {
        return ImageUtils.extractObjectKeyFromS3Url(url);
    }


}
