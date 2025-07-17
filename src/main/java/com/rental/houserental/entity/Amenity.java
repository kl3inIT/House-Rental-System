package com.rental.houserental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity extends BaseEntity {
    @Column(name = "Code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "Name", nullable = false, length = 100, columnDefinition = "NVARCHAR(100)")
    private String name;

    @Column(name = "Description", columnDefinition = "NVARCHAR(500)")
    private String description;

    @Column(name = "Icon", length = 255)
    private String icon;
}

