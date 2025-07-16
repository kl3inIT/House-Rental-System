package com.rental.houserental.dto.response.property;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyStatsDTO {
    private long totalProperties;
    private long availableCount;
    private long rentedCount;
    private long bookedCount;
    private long draftCount;
    private long unavailableCount;
    private long expiredCount;
    private long adminHiddenCount;
    private long adminBannedCount;
    private int totalViews;
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    public String getFormattedRevenue() {
        if (totalRevenue == null) return "0";
        if (totalRevenue.stripTrailingZeros().scale() <= 0) {
            return String.format("%,d", totalRevenue.longValue());
        } else {
            return String.format("%,.2f", totalRevenue);
        }
    }
}