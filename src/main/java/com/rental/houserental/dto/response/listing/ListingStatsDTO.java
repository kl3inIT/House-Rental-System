package com.rental.houserental.dto.response.listing;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingStatsDTO {

    private Long totalListings;
    private Long activeListings;
    private Long expiredListings;
    private Long upcomingListings;
    private Long highlightedListings;
    private Long totalViews;
    private Long totalInquiries;
    private Double totalRevenue;
} 