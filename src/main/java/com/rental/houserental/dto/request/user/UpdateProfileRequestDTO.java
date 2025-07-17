package com.rental.houserental.dto.request.user;

import com.rental.houserental.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDTO {
    @jakarta.validation.constraints.NotBlank(message = "Name is required")
    private String name;

    private String email;

    @jakarta.validation.constraints.Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;

    private String address;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;
    private Gender gender;
}