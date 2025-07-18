package com.rental.houserental.dto.request.systemconfig;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class SystemConfigUpdateRequestDTO {
    @NotNull
    @DecimalMin("0")
    private BigDecimal value;

    @NotNull
    @Size(min = 10, max = 100, message = "Description must be between 10 and 100 characters")
    private String description;

    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
} 