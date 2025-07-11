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
    private String location;
    private String mainImageUrl;
    private List<String> imageUrls;
    private Integer imageCount;
} 