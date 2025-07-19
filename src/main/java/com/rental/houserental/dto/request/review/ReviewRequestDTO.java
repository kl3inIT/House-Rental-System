package com.rental.houserental.dto.request.review;

import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long propertyId;
    private int star;
    private String description;
} 