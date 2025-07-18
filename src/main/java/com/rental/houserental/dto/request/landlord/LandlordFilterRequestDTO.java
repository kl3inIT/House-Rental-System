package com.rental.houserental.dto.request.landlord;

public class LandlordFilterRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String status; // ACTIVE, INACTIVE, ...
    private String sortBy; // status, balance, createdAt
    private String sortDir; // asc, desc
    private Double minBalance;
    private Double maxBalance;
    private java.time.LocalDate joinDateFrom;
    private java.time.LocalDate joinDateTo;
    private String joinDateSort; // "newest" hoặc "oldest"

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDir() { return sortDir; }
    public void setSortDir(String sortDir) { this.sortDir = sortDir; }
} 