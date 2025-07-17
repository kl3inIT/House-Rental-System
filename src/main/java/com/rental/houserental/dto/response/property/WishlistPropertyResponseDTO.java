package com.rental.houserental.dto.response.property;

import com.rental.houserental.enums.FurnishingType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistPropertyResponseDTO {

    private Long id;
    private String title;
    private BigDecimal monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal area;
    private String fullAddress;
    private String categoryName;
    private FurnishingType furnishing;
    private Integer depositPercentage;
    private Integer views;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
    private List<String> topAmenities; // Top 3-4 amenities for display
    private LocalDateTime addedToWishlistAt; // When added to wishlist
    
    // Helper methods for display
    public String getFormattedPrice() {
        if (monthlyRent == null) return "N/A";
        return String.format("%,d", monthlyRent.longValue());
    }
    
    public String getBedroomText() {
        if (bedrooms == null) return "N/A";
        return bedrooms == 0 ? "Studio" : bedrooms + (bedrooms == 1 ? " bedroom" : " bedrooms");
    }
    
    public String getBathroomText() {
        if (bathrooms == null) return "N/A";
        return bathrooms + (bathrooms == 1 ? " bathroom" : " bathrooms");
    }
    
    public String getAreaText() {
        if (area == null) return "N/A";
        return area + " m²";
    }
    
    public String getFurnishingText() {
        if (furnishing == null) return "Not specified";
        return switch (furnishing) {
            case FULL -> "Fully Furnished";
            case BASIC -> "Basic Furniture";
            case NONE -> "Unfurnished";
        };
    }
    
    public String getShortAddress() {
        if (fullAddress == null) return "";
        String[] parts = fullAddress.split(", ");
        if (parts.length >= 2) {
            // Return last 2 parts (Ward, Province)
            return parts[parts.length - 2] + ", " + parts[parts.length - 1];
        }
        return fullAddress;
    }
} 