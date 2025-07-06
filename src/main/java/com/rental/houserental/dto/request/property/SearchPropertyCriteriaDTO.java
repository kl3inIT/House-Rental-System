package com.rental.houserental.dto.request.property;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPropertyCriteriaDTO {
    private String province;
    private String ward;
    private String location; // Keep for backward compatibility
    
    // New filter structure
    private List<String> priceRanges; // e.g., ["0-1000000", "1000000-3000000"]
    private List<String> areaRanges; // e.g., ["0-50", "50-100"]
    private List<Long> propertyTypes; // Category IDs
    private Integer minBedrooms;
    private Integer maxBedrooms;
    private Integer minBathrooms;
    private Integer maxBathrooms;
    
    // Published date filters
    private LocalDate publishedFrom;
    private LocalDate publishedTo;
    private List<String> publishedRanges; // e.g., ["today", "week", "month", "3months"]
    
    // Legacy fields for backward compatibility
    private Long propertyType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minArea;
    private BigDecimal maxArea;
    
    private String keyword;
    private String status;
}
