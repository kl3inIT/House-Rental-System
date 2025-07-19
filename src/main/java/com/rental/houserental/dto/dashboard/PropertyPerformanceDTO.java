package com.rental.houserental.dto.dashboard;

import java.math.BigDecimal;

public class PropertyPerformanceDTO {
    private Long propertyId;
    private String propertyName;
    private String address;
    private BigDecimal monthlyRent;
    private Integer views;
    private Integer inquiries;

    public PropertyPerformanceDTO() {
    }

    public PropertyPerformanceDTO(Long propertyId, String propertyName, String address, BigDecimal monthlyRent,
            Integer views, Integer inquiries) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.address = address;
        this.monthlyRent = monthlyRent;
        this.views = views;
        this.inquiries = inquiries;
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getInquiries() {
        return inquiries;
    }

    public void setInquiries(Integer inquiries) {
        this.inquiries = inquiries;
    }
}