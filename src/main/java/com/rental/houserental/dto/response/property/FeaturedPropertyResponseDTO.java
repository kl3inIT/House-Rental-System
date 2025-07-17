package com.rental.houserental.dto.response.property;

import com.rental.houserental.enums.FurnishingType;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedPropertyResponseDTO {

    private Long id;
    private String title;
    private BigDecimal price;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal area;
    private String location;
    private String fullAddress;
    private String categoryName;
    private FurnishingType furnishing;
    private Integer depositPercentage;
    private Integer views;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
    private List<String> topAmenities; // Top 3-4 amenities for display
    private Double rating; // Average rating if available
    private Integer reviewCount;
    
    // Helper method to format price
    public String getFormattedPrice() {
        if (price == null) return "N/A";
        return String.format("%,d", price.longValue());
    }
    
    // Helper method for bedroom text
    public String getBedroomText() {
        if (bedrooms == null) return "N/A";
        return bedrooms == 0 ? "Studio" : bedrooms + (bedrooms == 1 ? " bedroom" : " bedrooms");
    }
    
    // Helper method for bathroom text
    public String getBathroomText() {
        if (bathrooms == null) return "N/A";
        return bathrooms + (bathrooms == 1 ? " bathroom" : " bathrooms");
    }
    
    // Helper method for area text
    public String getAreaText() {
        if (area == null) return "N/A";
        return area + " m²";
    }
    
    // Helper method for furnishing text
    public String getFurnishingText() {
        if (furnishing == null) return "Not specified";
        return switch (furnishing) {
            case FULL -> "Fully Furnished";
            case BASIC -> "Basic Furniture";
            case NONE -> "Unfurnished";
        };
    }
} 