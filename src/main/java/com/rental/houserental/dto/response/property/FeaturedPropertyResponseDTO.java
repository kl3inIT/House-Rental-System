package com.rental.houserental.dto.response.property;

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
    private String ward;
    private String province;
    private String location; // Keep for backward compatibility
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
} 