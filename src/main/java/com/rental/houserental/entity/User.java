package com.rental.houserental.entity;

import com.rental.houserental.enums.Gender;
import com.rental.houserental.enums.UserRole;
import com.rental.houserental.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "[User]")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(name = "FullName")
    private String name;

    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @Column(name = "Password")
    private String password;

    @Column(name = "PhoneNumber")
    private String phone;

    @Column(name = "Address")
    private String address;

    @Column(name = "DateOfBirth")
    private LocalDateTime dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "Gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    @Builder.Default
    private UserStatus status = UserStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "Role")
    @Builder.Default
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "landlord", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RentalProperty> rentalProperties;
}
