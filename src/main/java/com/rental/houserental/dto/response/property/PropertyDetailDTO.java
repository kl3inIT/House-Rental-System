package com.rental.houserental.dto.response.property;

import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.enums.PropertyStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetailDTO {
    private Long id;
    private String title;
    private String description;

    private String streetAddress;
    private String ward;
    private String province;
    private String fullAddress;

    private Long categoryId;

    private BigDecimal monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal area;

    private PropertyStatus propertyStatus;

    private String mainImageUrl;
    private List<String> imageUrls;

    private FurnishingType furnishing;

    private Integer depositPercentage;

    private Double latitude;
    private Double longitude;

    private Integer views;
    private Double rating;

    private LocalDateTime publishAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String landlordName;
    private Long landlordId;
    private List<AmenityDTO> amenities;

    private LocalDateTime listingStartDate;
    private LocalDateTime listingEndDate;
    private Long timeLeftDays;
}
