package com.rental.houserental.service.impl;

import com.rental.houserental.entity.SystemConfig;
import com.rental.houserental.repository.SystemConfigRepository;
import com.rental.houserental.service.SystemConfigService;
import com.rental.houserental.dto.response.systemconfig.SystemConfigResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private final SystemConfigRepository systemConfigRepository;
    
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
    public BigDecimal getNormalListingPricePerWeek() {
        return getConfigValueAsDecimal("listing.price.normal.week", new BigDecimal("30000"));
    }
    
    @Override
    public BigDecimal getHighlightListingPricePerWeek() {
        return getConfigValueAsDecimal("listing.price.highlight.week", new BigDecimal("50000"));
    }

    @Override
    public BigDecimal getNormalListingPricePerMonth() {
        return getConfigValueAsDecimal("listing.price.normal.month", new BigDecimal("100000"));
    }

    @Override
    public BigDecimal getHighlightListingPricePerMonth() {
        return getConfigValueAsDecimal("listing.price.highlight.month", new BigDecimal("150000"));
    }

    @Override
    public List<SystemConfigResponseDTO> getAllConfigs() {
        return systemConfigRepository.findAll().stream()
            .map(this::toResponseDTO)
            .toList();
    }

    @Override
    public SystemConfigResponseDTO getConfigById(Long id) {
        return systemConfigRepository.findById(id)
            .map(this::toResponseDTO)
            .orElseThrow();
    }

    private SystemConfigResponseDTO toResponseDTO(SystemConfig config) {
        SystemConfigResponseDTO dto = new SystemConfigResponseDTO();
        dto.setId(config.getId());
        dto.setKey(config.getConfigKey());
        dto.setValue(config.getDecimalValue());
        dto.setDescription(config.getDescription());
        return dto;
    }

    @Override
    public void updateConfigValueAndDescription(Long id, BigDecimal value, String description) {
        SystemConfig config = systemConfigRepository.findById(id).orElseThrow();
        config.setConfigValue(value.toPlainString());
        config.setDescription(description);
        systemConfigRepository.save(config);
    }
} 