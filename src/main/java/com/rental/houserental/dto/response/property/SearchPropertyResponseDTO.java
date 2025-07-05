package com.rental.houserental.dto.response.property;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchPropertyResponseDTO {

    private Long id;
    private String title;
    private BigDecimal monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private String streetAddress;
    private String city;
    private String province;
    private String description;
    private String propertyStatus;
    private String categoryName;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
    
    // Computed fields
    public String getFullAddress() {
        return String.format("%s, %s, %s", streetAddress, city, province);
    }
    
    public String getAvailabilityStatus() {
        return "Available Now"; // You can enhance this based on property status
    }
}

