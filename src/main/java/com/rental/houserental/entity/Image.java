package com.rental.houserental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Image")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Image extends BaseEntity {

    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "RentalPropertyId")
    private RentalProperty rentalProperty;
}
