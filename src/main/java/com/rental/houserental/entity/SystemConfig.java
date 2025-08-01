package com.rental.houserental.entity;

import com.rental.houserental.enums.ConfigType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "SystemConfig")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfig extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ConfigKey", unique = true, nullable = false)
    private String configKey;
    
    @Column(name = "ConfigValue", nullable = false)
    private String configValue;
    
    @Column(name = "description", length = 100,columnDefinition = "NVARCHAR(100)")
    private String description;
    
    @Column(name = "ConfigType")
    @Enumerated(EnumType.STRING)
    private ConfigType configType;
    
    @Column(name = "IsActive")
    private Boolean isActive = true;
    
    public BigDecimal getDecimalValue() {
        try {
            return new BigDecimal(configValue);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
} 