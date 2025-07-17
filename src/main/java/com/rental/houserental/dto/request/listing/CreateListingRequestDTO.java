package com.rental.houserental.dto.request.listing;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateListingRequestDTO {

    @NotNull(message = "Property is required")
    private Long rentalPropertyId;

    @NotNull(message = "Start date is required")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @Builder.Default
    private Boolean isHighlight = false;

    @NotNull(message = "Rental amount is required")
    private Integer duration;

    @NotBlank(message = "Duration type is required")
    private String durationType;
} 