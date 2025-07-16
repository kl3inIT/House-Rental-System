package com.rental.houserental.dto.response.listing;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingPriceDTO {
    private BigDecimal normalPricePerMonth;
    private BigDecimal highlightPricePerMonth;
    private BigDecimal normalPricePerWeek;
    private BigDecimal highlightPricePerWeek;
}
