package com.rental.houserental.utils;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public final class ImageUtils {

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "webp"};
    private static final long MAX_FILE_SIZE = 10L * 1024 * 1024;
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "image/webp"};

    private ImageUtils() {
        // Prevent instantiation
    }

    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!isValidExtension(extension)) {
            throw new IllegalArgumentException("Only JPG, JPEG, PNG, and WEBP files are allowed");
        }

        String contentType = file.getContentType();
        if (!isValidMimeType(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Please upload a valid image file.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }
    }

    public static String generateUniqueFileName(String originalFilename) {
        if (originalFilename == null) {
            throw new IllegalArgumentException("Original filename cannot be null");
        }

        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        return String.format("%s_%s.%s", timestamp, uuid, extension);
    }

    public static String getFileExtension(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 && lastDotIndex < filename.length() - 1 
            ? filename.substring(lastDotIndex + 1) 
            : "";
    }

    public static String extractObjectKeyFromS3Url(String s3Url) {
        if (s3Url == null || s3Url.trim().isEmpty()) {
            return "";
        }
        
        String[] parts = s3Url.split(".amazonaws.com/");
        return parts.length > 1 ? parts[1] : "";
    }

    private static boolean isValidExtension(String extension) {
        if (extension == null) {
            return false;
        }
        
        String lowerExtension = extension.toLowerCase();
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(lowerExtension)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }
        
        for (String allowedType : ALLOWED_MIME_TYPES) {
            if (allowedType.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }
} 