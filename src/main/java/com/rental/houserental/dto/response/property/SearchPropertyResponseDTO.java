package com.rental.houserental.dto.response.property;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPropertyResponseDTO {
    private Long id;
    private String title;
    private BigDecimal monthlyRent;
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal area;
    private String streetAddress;
    private String ward;
    private String province;
    private String fullAddress; // Computed full address
    private String description;
    private String propertyStatus;
    private String categoryName;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
    private LocalDateTime publishedAt;
}

