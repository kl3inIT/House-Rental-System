package com.rental.houserental.entity;

import com.rental.houserental.enums.PropertyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "RentalProperty")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentalProperty extends BaseEntity {

    @Column(name = "Title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CategoryId", nullable = false)
    private Category category;

    @Column(name = "MonthlyRent", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyRent;

    @Column(name = "Bedrooms", nullable = false)
    private Integer bedrooms;

    @Column(name = "Bathrooms", nullable = false)
    private Integer bathrooms;

    @Column(name = "Area", nullable = false, precision = 8, scale = 2)
    private BigDecimal area;

    @Column(name = "StreetAddress", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String streetAddress;

    @Column(name = "Ward", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String ward;

    @Column(name = "Province", nullable = false, columnDefinition = "NVARCHAR(100)")
    private String province;

    @Column(name = "Description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "PropertyStatus")
    @Builder.Default
    private PropertyStatus propertyStatus = PropertyStatus.DRAFT;

    @ManyToOne
    @JoinColumn(name = "LandlordId", nullable = false)
    private User landlord;

    @Column(name = "PublishedAt")
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "rentalProperty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC, createdAt ASC")
    private Set<PropertyImage> images;

    public PropertyImage getMainImage() {
        return images.stream()
                .filter(PropertyImage::getIsMainImage)
                .findFirst()
                .orElse(images.stream().findFirst().orElse(null));
    }

    public String getMainImageUrl() {
        PropertyImage mainImage = getMainImage();
        return mainImage != null ? mainImage.getImageUrl() : null;
    }

    public String getFullAddress() {
        return String.format("%s, %s, %s", streetAddress, ward, province);
    }
}
