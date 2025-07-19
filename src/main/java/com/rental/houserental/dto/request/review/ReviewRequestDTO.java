package com.rental.houserental.dto.request.review;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequestDTO {
    private Long propertyId;

    @NotNull(message = "Star is required")
    @Min(value = 1, message = "Star must be at least 1")
    @Max(value = 5, message = "Star must be at most 5")
    private int star;

    //@NotBlank(message = "Description is required") // Bỏ bắt buộc
    //@Size(max = 500, message = "Description cannot exceed 500 characters") // Đổi sang 500 từ
    @Size(max = 500, message = "Description cannot exceed 500 words")
    private String description;
} 