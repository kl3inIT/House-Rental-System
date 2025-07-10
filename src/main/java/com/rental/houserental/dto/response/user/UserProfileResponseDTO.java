package com.rental.houserental.dto.response.user;

import com.rental.houserental.enums.Gender;
import com.rental.houserental.enums.UserRole;
import com.rental.houserental.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime dateOfBirth;
    private double balance;
    private Gender gender;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}