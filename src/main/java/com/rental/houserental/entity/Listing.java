package com.rental.houserental.entity;

import com.rental.houserental.enums.ListingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Listings")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Listing extends BaseEntity {

    @Column(name = "StartDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "Amount", nullable = false)
    private double amount;

    @Column(name = "Description", columnDefinition = "NVARCHAR(2000)")
    private String description;

    @Column(name = "IsHighlight", nullable = false)
    private boolean isHighlight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PropertyId", nullable = false)
    private RentalProperty rentalProperty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LandlordId", nullable = false)
    private User landlord;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ListingStatus status;
}
