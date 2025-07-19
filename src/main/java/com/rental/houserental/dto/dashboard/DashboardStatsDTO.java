package com.rental.houserental.dto.dashboard;

public class DashboardStatsDTO {
    private Long totalProperties;
    private Long bookingsThisMonth;
    private Long activeBookings;
    private Long monthlyRevenue;

    public DashboardStatsDTO() {
    }

    public DashboardStatsDTO(Long totalProperties, Long bookingsThisMonth, Long activeBookings, Long monthlyRevenue) {
        this.totalProperties = totalProperties;
        this.bookingsThisMonth = bookingsThisMonth;
        this.activeBookings = activeBookings;
        this.monthlyRevenue = monthlyRevenue;
    }

    public Long getTotalProperties() {
        return totalProperties;
    }

    public void setTotalProperties(Long totalProperties) {
        this.totalProperties = totalProperties;
    }

    public Long getBookingsThisMonth() {
        return bookingsThisMonth;
    }

    public void setBookingsThisMonth(Long bookingsThisMonth) {
        this.bookingsThisMonth = bookingsThisMonth;
    }

    public Long getActiveBookings() {
        return activeBookings;
    }

    public void setActiveBookings(Long activeBookings) {
        this.activeBookings = activeBookings;
    }

    public Long getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(Long monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }
}