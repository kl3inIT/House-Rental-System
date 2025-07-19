package com.rental.houserental.dto.response.review;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponseDTO {
    private Long id;
    private String description;
    private int star;
    private String tenantName;
    private String tenantEmail;
    private Long propertyId;
    private String propertyName;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
} 