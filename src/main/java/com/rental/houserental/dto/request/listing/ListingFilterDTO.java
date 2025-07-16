package com.rental.houserental.dto.request.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingFilterDTO {

    private String searchTerm;
    private String status; // active, expired, upcoming
    private String highlight;
    private Long propertyId;
    private String sortBy; // amount, startDate, endDate, createdAt
    private String sortDirection; // asc, desc
} 