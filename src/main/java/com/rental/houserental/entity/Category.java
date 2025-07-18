package com.rental.houserental.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Categories")
public class Category extends BaseEntity {

    @Column(name = "Name", length = 80, nullable = false, columnDefinition = "NVARCHAR(80)")
    private String name;
    @Column(name = "Description", length = 200, nullable = false, columnDefinition = "NVARCHAR(200)")
    private String description;

}
