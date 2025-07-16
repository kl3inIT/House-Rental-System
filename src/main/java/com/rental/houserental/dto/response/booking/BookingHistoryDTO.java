package com.rental.houserental.dto.response.booking;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistoryDTO {
    private Long bookingId;
    private Long propertyId;
    private String propertyImage;
    private String propertyName;
    private String propertyAddress;
    private String bookingDate;
    private String bookingStatus;
    private Double totalAmount;
}
