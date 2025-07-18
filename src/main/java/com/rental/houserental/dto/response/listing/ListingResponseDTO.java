package com.rental.houserental.dto.response.listing;

import lombok.Data;

@Data
public class ListingResponseDTO {
    private Long id;
    private String title;
    private String landlordName;
    private String categoryName;
    private Double amount;
    private String createdAt;
    private String status;
} 