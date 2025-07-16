package com.rental.houserental.dto.request.transaction;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionRequestDTO {

    private String type;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amountFrom = null;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amountTo = null;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTo;

    private String userEmail;

    private int page = 0;
    private int size = 4;


}
