package com.rental.houserental.dto.request.property;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyBookingRequestDTO {
    private Long id;
    private String renterEmail;
    private String landlordEmail;
    private Double depositAmount;
    private Double totalAmount;
    private String startDate;
    private Long durationInMonths;
    private String note;

}
