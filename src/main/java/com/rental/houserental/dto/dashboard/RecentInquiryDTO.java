package com.rental.houserental.dto.dashboard;

import java.time.LocalDateTime;

public class RecentInquiryDTO {
    private String tenantName;
    private String propertyName;
    private LocalDateTime createdAt;
    private String status;

    public RecentInquiryDTO() {
    }

    public RecentInquiryDTO(String tenantName, String propertyName, LocalDateTime createdAt, String status) {
        this.tenantName = tenantName;
        this.propertyName = propertyName;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}