package com.rental.houserental.service;

import com.rental.houserental.entity.SystemConfig;
import com.rental.houserental.dto.response.systemconfig.SystemConfigResponseDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SystemConfigService {
    
    Optional<SystemConfig> getConfigByKey(String key);

    BigDecimal getConfigValueAsDecimal(String key, BigDecimal defaultValue);

    BigDecimal getNormalListingPricePerMonth();
    
    BigDecimal getHighlightListingPricePerMonth();

    BigDecimal getNormalListingPricePerWeek();

    BigDecimal getHighlightListingPricePerWeek();

    List<SystemConfigResponseDTO> getAllConfigs();

    SystemConfigResponseDTO getConfigById(Long id);
    
    void updateConfigValueAndDescription(Long id, BigDecimal value, String description);
} 