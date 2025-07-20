package com.rental.houserental.dto.request.property;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPropertyCriteriaDTO {
    private String location;
    private String province;
    private String ward;
    private String keyword;
    private String status;
    
    // Multiple statuses support
    @Builder.Default
    private List<String> statuses = new ArrayList<>();
    
    // Price filters
    @Builder.Default
    private List<String> priceRanges = new ArrayList<>();
    
    // Area filters
    @Builder.Default
    private List<String> areaRanges = new ArrayList<>();
    
    // Property type filters
    @Builder.Default
    private List<Long> propertyTypes = new ArrayList<>();
    
    // Bedroom filters
    private Integer minBedrooms;
    private Integer maxBedrooms;
    
    // Bathroom filters
    private Integer minBathrooms;
    private Integer maxBathrooms;
    

    // Pagination and sorting
    private Integer page;
    private String sortBy;
    private String sortDirection; // "asc" or "desc"
}
