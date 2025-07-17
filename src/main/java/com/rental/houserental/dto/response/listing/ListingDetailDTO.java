package com.rental.houserental.dto.response.listing;

import com.rental.houserental.enums.ListingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingDetailDTO {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double amount;
    private String description;
    private Boolean isHighlight;
    private String status; 
    
    private Long propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String propertyImageUrl;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer viewCount;
    private Integer inquiryCount;
} 