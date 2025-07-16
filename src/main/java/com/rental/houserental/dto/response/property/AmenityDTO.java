package com.rental.houserental.dto.response.property;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AmenityDTO {
    private Long id;
    private String code;
    private String name;
    private String icon;
    private String description;
}