package com.rental.houserental.dto.response.property;


import com.rental.houserental.enums.PropertyStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyListItemDTO {
    private Long id;
    private String title;
    private PropertyStatus propertyStatus;
    private String mainImageUrl;
    private String fullAddress;
    private BigDecimal monthlyRent;
    private BigDecimal area;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer views;
    private LocalDateTime createAt;

    public String getFormattedRevenue() {
        if (monthlyRent == null) return "0";
        if (monthlyRent.stripTrailingZeros().scale() <= 0) {
            return String.format("%,d", monthlyRent.longValue());
        } else {
            return String.format("%,.2f", monthlyRent);
        }
    }
}
