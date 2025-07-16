package com.rental.houserental.dto.response.booking;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistoryDetailDTO {

    private Long bookingId;

    private Long propertyId;
    private String propertyImage;
    private String propertyName;
    private String propertyAddress;

    private String renterEmail;
    private String renterPhone;
    private String renterName;

    private String landlordEmail;
    private String landlordPhone;
    private String landlordName;

    private String startDate;
    private String endDate;
    private String bookingStatus;
    private LocalDateTime bookingDate;
    private String note;

    private Double depositAmount;
    private Double totalAmount;
    private Double propertyPrice;
    private int durationInMonths;



}
