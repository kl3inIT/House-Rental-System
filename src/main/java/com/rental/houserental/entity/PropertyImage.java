package com.rental.houserental.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PropertyImages")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyImage extends BaseEntity {

    @Column(name = "ImageUrl", nullable = false)
    private String imageUrl;

    @Column(name = "FileName")
    private String fileName;

    @Column(name = "FileSize")
    private Long fileSize;

    @Column(name = "IsMainImage")
    @Builder.Default
    private Boolean isMainImage = false;

    @Column(name = "DisplayOrder")
    @Builder.Default
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RentalPropertyId", nullable = false)
    private RentalProperty rentalProperty;
}
