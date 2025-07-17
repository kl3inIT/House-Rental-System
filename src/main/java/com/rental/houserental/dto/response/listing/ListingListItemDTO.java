package com.rental.houserental.dto.response.listing;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingListItemDTO {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double amount;
    private String description;
    private Boolean isHighlight;
    private String status;
    
    // Property information
    private Long propertyId;
    private String propertyTitle;
    private String propertyAddress;
    private String propertyMainImageUrl;
    
    private LocalDateTime createdAt;
    private Integer viewCount;
    private Integer inquiryCount;
} 