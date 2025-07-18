package com.rental.houserental.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequestDTO {
    @NotBlank(message = "Name category is required")
    @Size(max = 80, message = "Name category must be less than 80 characters")
    private String name;
    @Size(max = 200, message = "Description must be less than 200 characters")
    private String description;
} 