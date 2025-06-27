package com.rental.houserental.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePropertyRequest {

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
    @DecimalMin(value = "0.5", message = "Bathrooms must be at least 0.5")
    @DecimalMax(value = "10.0", message = "Bathrooms must be 10.0 or less")
    private Double bathrooms;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address must be less than 255 characters")
    private String streetAddress;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be less than 100 characters")
    private String city;

    @NotBlank(message = "Province is required")
    @Size(max = 100, message = "Province must be less than 100 characters")
    private String province;

    @NotBlank(message = "Property description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    @DecimalMin(value = "0.0", message = "Security deposit must be 0 or greater")
    @Digits(integer = 10, fraction = 2, message = "Invalid security deposit amount")
    private BigDecimal securityDeposit;

    @Min(value = 1, message = "Minimum lease duration must be at least 1 month")
    @Max(value = 60, message = "Minimum lease duration must be 60 months or less")
    private Integer minLeaseDuration;
} 