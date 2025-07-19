package com.rental.houserental.dto.request.review;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewSearchRequestDTO {
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    private String tenantName;
    private Integer star;
    private Long categoryId;
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
} 