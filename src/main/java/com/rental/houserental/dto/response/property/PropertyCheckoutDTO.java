package com.rental.houserental.dto.response.property;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PropertyCheckoutDTO {

    private Long id;
    private String image;
    private String name;
    private String address;
    private Double price;
    private Long percentageDeposit;

    private String renterEmail;
    private String renterPhone;
    private String renterName;

    private String landlordEmail;
    private String landlordPhone;
    private String landlordName;

    private Double renterBalance;

}
