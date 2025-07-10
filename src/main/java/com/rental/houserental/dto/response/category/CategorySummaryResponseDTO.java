package com.rental.houserental.dto.response.category;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategorySummaryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private int totalProperties;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 