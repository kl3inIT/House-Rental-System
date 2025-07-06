package com.rental.houserental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Reviews")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseEntity {

    @Column(name = "Star", nullable = false)
    private int star;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PropertyId", nullable = false)
    private RentalProperty rentalProperty;
}
