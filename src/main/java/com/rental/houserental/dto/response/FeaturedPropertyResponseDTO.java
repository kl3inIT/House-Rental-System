package com.rental.houserental.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeaturedPropertyResponseDTO {

    private Long id;
    private String title;
    private BigDecimal price; // monthlyRent
    private Integer bedrooms;
    private Integer bathrooms;
    private String location; // city + province
    private String imageUrl; // main image URL

} 