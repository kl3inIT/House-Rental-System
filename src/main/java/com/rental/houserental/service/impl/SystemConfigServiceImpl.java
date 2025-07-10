package com.rental.houserental.service.impl;

import com.rental.houserental.entity.SystemConfig;
import com.rental.houserental.enums.ConfigType;
import com.rental.houserental.repository.SystemConfigRepository;
import com.rental.houserental.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private final SystemConfigRepository systemConfigRepository;
    
    // Configuration keys
    private static final String NORMAL_LISTING_PRICE_KEY = "listing.price.normal";
    private static final String HIGHLIGHT_LISTING_PRICE_KEY = "listing.price.highlight";

    
    @Override
    public Optional<SystemConfig> getConfigByKey(String key) {
        return systemConfigRepository.findActiveByKey(key);
    }
    
    @Override
    public BigDecimal getConfigValueAsDecimal(String key, BigDecimal defaultValue) {
        return getConfigByKey(key)
                .map(SystemConfig::getDecimalValue)
                .orElse(defaultValue);
    }
    
    @Override
    @Transactional
    public void updateConfig(String key, String value) {
        SystemConfig config = getConfigByKey(key)
                .orElse(createNewConfig(key, value, ConfigType.OTHER));
        config.setConfigValue(value);
        systemConfigRepository.save(config);
    }
    
    @Override
    @Transactional
    public void updateConfig(String key, BigDecimal value) {
        updateConfig(key, value.toString());
    }
    
    @Override
    public BigDecimal getNormalListingPricePerWeek() {
        return getConfigValueAsDecimal(NORMAL_LISTING_PRICE_KEY, new BigDecimal("30000"));
    }
    
    @Override
    public BigDecimal getHighlightListingPricePerWeek() {
        return getConfigValueAsDecimal(HIGHLIGHT_LISTING_PRICE_KEY, new BigDecimal("50000"));
    }

    @Override
    public BigDecimal getNormalListingPricePerMonth() {
        return getConfigValueAsDecimal(NORMAL_LISTING_PRICE_KEY, new BigDecimal("100000"));
    }

    @Override
    public BigDecimal getHighlightListingPricePerMonth() {
        return getConfigValueAsDecimal(HIGHLIGHT_LISTING_PRICE_KEY, new BigDecimal("150000"));
    }
    
    @Override
    @Transactional
    public void setNormalListingPrice(BigDecimal price) {
        updateConfig(NORMAL_LISTING_PRICE_KEY, price);
    }
    
    @Override
    @Transactional
    public void setHighlightListingPrice(BigDecimal price) {
        updateConfig(HIGHLIGHT_LISTING_PRICE_KEY, price);
    }
    
    private SystemConfig createNewConfig(String key, String value, ConfigType type) {
        SystemConfig config = new SystemConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setConfigType(type);
        config.setIsActive(true);
        config.setDescription("Auto-generated configuration");
        return config;
    }

} 