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

    @Column(name = "Name")
    private String name;
    @Column(name = "Description")
    private String description;

}
