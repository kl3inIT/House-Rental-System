package com.rental.houserental.dto.request.property;

import com.rental.houserental.enums.FurnishingType;
import jakarta.validation.constraints.NotBlank;


import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePropertyRequestDTO {
    @NotBlank(message = "Property title is required")
    @Size(max = 255, message = "Title must be less than 255 characters")
    private String title;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Monthly rent is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly rent must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Invalid rent amount")
    private BigDecimal monthlyRent;

    @NotNull(message = "Number of bedrooms is required")
    @Min(value = 0, message = "Bedrooms must be 0 or greater")
    @Max(value = 10, message = "Bedrooms must be 10 or less")
    private Integer bedrooms;

    @NotNull(message = "Number of bathrooms is required")
    @Min(value = 1, message = "Bathrooms must be at least 1")
    @Max(value = 10, message = "Bathrooms must be 10 or less")
    private Integer bathrooms;

    @NotNull(message = "Property area is required")
    @DecimalMin(value = "1.0", inclusive = true, message = "Area must be at least 1 square meter")
    @DecimalMax(value = "10000.0", inclusive = true, message = "Area must be less than 10,000 square meters")
    @Digits(integer = 6, fraction = 2, message = "Invalid area value")
    private BigDecimal area;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must be less than 255 characters")
    private String streetAddress;

    @NotBlank(message = "Ward is required")
    @Size(max = 100, message = "Ward must be less than 100 characters")
    private String ward;

    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province must be less than 100 characters")
    private String province;

    @NotBlank(message = "Property description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @NotNull(message = "Furnishing is required")
    private FurnishingType furnishing; // FULL, BASIC, NONE

    @Min(value = 10, message = "Deposit percentage must be at least 10")
    @Max(value = 50, message = "Deposit percentage must be at most 50")
    private Integer depositPercentage;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;
    private String mainImageUrl;
    private List<String> imageUrls;
    private List<Long> amenityIds;
}
