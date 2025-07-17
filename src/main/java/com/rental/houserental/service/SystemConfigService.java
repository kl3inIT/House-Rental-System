package com.rental.houserental.service;

import com.rental.houserental.entity.SystemConfig;

import java.math.BigDecimal;
import java.util.Optional;

public interface SystemConfigService {
    
    Optional<SystemConfig> getConfigByKey(String key);

    BigDecimal getConfigValueAsDecimal(String key, BigDecimal defaultValue);

    BigDecimal getNormalListingPricePerMonth();
    
    BigDecimal getHighlightListingPricePerMonth();

    BigDecimal getNormalListingPricePerWeek();

    BigDecimal getHighlightListingPricePerWeek();
} 