package com.rental.houserental.entity;

import com.rental.houserental.enums.PropertyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "RentalProperty")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentalProperty extends BaseEntity {

    @Column(name = "Description")
    private String description;

    @Column(name = "PropertyName")
    private String propertyName;

    @Column(name = "Address")
    private String address;

    @Column(name = "City")
    private String city;

    @Column(name = "Price")
    private Double price;

    @Enumerated(EnumType.STRING)
    private PropertyStatus propertyStatus;

    @ManyToOne
    @JoinColumn(name = "LandlordId")
    private User landlord;

    @OneToMany(mappedBy = "rentalProperty", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Image> images;
}
