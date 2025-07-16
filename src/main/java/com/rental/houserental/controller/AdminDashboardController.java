package com.rental.houserental.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rental.houserental.entity.LandlordUpgradeRequest;
import com.rental.houserental.enums.LandlordUpgradeRequestStatus;
import com.rental.houserental.service.LandlordUpgradeRequestService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final LandlordUpgradeRequestService requestService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Admin Dashboard - RentEase");

        // Dashboard Statistics - using hardcoded values for now
        Map<String, Object> dashboardStats = new HashMap<>();
        dashboardStats.put("totalUsers", 2847);
        dashboardStats.put("totalProperties", 1234);
        dashboardStats.put("activeBookings", 567);
        dashboardStats.put("monthlyRevenue", new BigDecimal("124500.00"));
        dashboardStats.put("pendingLandlordRequests", 3);
        dashboardStats.put("flaggedReviews", 2);

        // Recent Activity (mock data for now)
        List<Map<String, Object>> recentActivity = new ArrayList<>();

        Map<String, Object> activity1 = new HashMap<>();
        activity1.put("type", "USER_REGISTRATION");
        activity1.put("message", "New user registration: John Doe");
        activity1.put("time", "2 minutes ago");
        activity1.put("color", "green");
        recentActivity.add(activity1);

        Map<String, Object> activity2 = new HashMap<>();
        activity2.put("type", "PROPERTY_LISTED");
        activity2.put("message", "Property listed: Modern Apartment Downtown");
        activity2.put("time", "15 minutes ago");
        activity2.put("color", "blue");
        recentActivity.add(activity2);

        Map<String, Object> activity3 = new HashMap<>();
        activity3.put("type", "BOOKING_REQUEST");
        activity3.put("message", "Booking request submitted for Suburban House");
        activity3.put("time", "1 hour ago");
        activity3.put("color", "yellow");
        recentActivity.add(activity3);

        Map<String, Object> activity4 = new HashMap<>();
        activity4.put("type", "PAYMENT_PROCESSED");
        activity4.put("message", "Payment processed: $1,800");
        activity4.put("time", "2 hours ago");
        activity4.put("color", "purple");
        recentActivity.add(activity4);

        Map<String, Object> activity5 = new HashMap<>();
        activity5.put("type", "PROPERTY_REPORTED");
        activity5.put("message", "Property reported: Studio Apartment");
        activity5.put("time", "3 hours ago");
        activity5.put("color", "red");
        recentActivity.add(activity5);

        // System Status (mock data for now)
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put("serverStatus", "Online");
        systemStatus.put("databaseStatus", "Healthy");
        systemStatus.put("paymentGatewayStatus", "Active");
        systemStatus.put("emailServiceStatus", "Warning");
        systemStatus.put("storageUsage", 78);
        systemStatus.put("storageUsed", "234 GB");
        systemStatus.put("storageTotal", "300 GB");

        model.addAttribute("dashboardStats", dashboardStats);
        model.addAttribute("recentActivity", recentActivity);
        model.addAttribute("systemStatus", systemStatus);

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Users - Admin Dashboard");

        // Mock user data
        List<Map<String, Object>> users = new ArrayList<>();

        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", 1L);
        user1.put("name", "John Doe");
        user1.put("email", "john.doe@email.com");
        user1.put("type", "Renter");
        user1.put("status", "Active");
        user1.put("joined", "Jan 15, 2024");
        user1.put("lastActive", "2 hours ago");
        users.add(user1);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", 2L);
        user2.put("name", "Sarah Johnson");
        user2.put("email", "sarah.johnson@email.com");
        user2.put("type", "Landlord");
        user2.put("status", "Active");
        user2.put("joined", "Mar 22, 2020");
        user2.put("lastActive", "1 day ago");
        users.add(user2);

        Map<String, Object> user3 = new HashMap<>();
        user3.put("id", 3L);
        user3.put("name", "Michael Chen");
        user3.put("email", "michael.chen@email.com");
        user3.put("type", "Renter");
        user3.put("status", "Pending");
        user3.put("joined", "Nov 28, 2024");
        user3.put("lastActive", "5 hours ago");
        users.add(user3);

        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/properties")
    public String properties(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Properties - Admin Dashboard");

        // Mock property data
        List<Map<String, Object>> properties = new ArrayList<>();

        Map<String, Object> property1 = new HashMap<>();
        property1.put("id", 1L);
        property1.put("name", "Modern Downtown Apartment");
        property1.put("address", "123 Main St, Downtown, New York, NY 10001");
        property1.put("landlord", "Sarah Johnson");
        property1.put("landlordEmail", "sarah.j@email.com");
        property1.put("beds", 2);
        property1.put("baths", 2);
        property1.put("sqft", 1200);
        property1.put("rent", new BigDecimal("1800.00"));
        property1.put("status", "Active");
        property1.put("listedDate", "Nov 15, 2024");
        property1.put("views", 45);
        property1.put("inquiries", 8);
        property1.put("rating", 4.8);
        property1.put("reports", 0);
        properties.add(property1);

        Map<String, Object> property2 = new HashMap<>();
        property2.put("id", 2L);
        property2.put("name", "Luxury Suburban House");
        property2.put("address", "456 Oak Ave, Suburbs, California, CA 90210");
        property2.put("landlord", "Michael Thompson");
        property2.put("landlordEmail", "m.thompson@email.com");
        property2.put("beds", 4);
        property2.put("baths", 3);
        property2.put("sqft", 2500);
        property2.put("rent", new BigDecimal("3500.00"));
        property2.put("status", "Pending Review");
        property2.put("listedDate", "Nov 28, 2024");
        property2.put("views", 0);
        property2.put("inquiries", 0);
        property2.put("rating", 0.0);
        property2.put("reports", 0);
        properties.add(property2);

        model.addAttribute("properties", properties);
        return "admin/properties";
    }

    @GetMapping("/bookings")
    public String bookings(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Bookings - Admin Dashboard");

        // Mock booking data
        List<Map<String, Object>> bookings = new ArrayList<>();

        Map<String, Object> booking1 = new HashMap<>();
        booking1.put("id", "BK-2024-001");
        booking1.put("tenantName", "John Doe");
        booking1.put("tenantEmail", "john.doe@email.com");
        booking1.put("propertyName", "Downtown Apartment");
        booking1.put("propertyAddress", "123 Main St, NY");
        booking1.put("landlordName", "Sarah Johnson");
        booking1.put("landlordEmail", "sarah.j@email.com");
        booking1.put("amount", new BigDecimal("1800.00"));
        booking1.put("status", "Active");
        booking1.put("date", "Nov 15, 2024");
        bookings.add(booking1);

        Map<String, Object> booking2 = new HashMap<>();
        booking2.put("id", "BK-2024-002");
        booking2.put("tenantName", "Emily Rodriguez");
        booking2.put("tenantEmail", "emily.r@email.com");
        booking2.put("propertyName", "Suburban House");
        booking2.put("propertyAddress", "456 Oak Ave, CA");
        booking2.put("landlordName", "Michael Thompson");
        booking2.put("landlordEmail", "m.thompson@email.com");
        booking2.put("amount", new BigDecimal("3500.00"));
        booking2.put("status", "Pending");
        booking2.put("date", "Nov 28, 2024");
        bookings.add(booking2);

        // Calculate counts
        long activeCount = bookings.stream()
                .filter(b -> "Active".equals(b.get("status")))
                .count();
        long pendingCount = bookings.stream()
                .filter(b -> "Pending".equals(b.get("status")))
                .count();
        BigDecimal totalRevenue = bookings.stream()
                .map(b -> (BigDecimal) b.get("amount"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("bookings", bookings);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("totalRevenue", totalRevenue);
        return "admin/bookings";
    }

    @GetMapping("/categories")
    public String categories(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Categories - Admin Dashboard");

        // Mock category data
        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 1L);
        category1.put("name", "Apartment");
        category1.put("description", "Multi-unit residential buildings");
        category1.put("propertyCount", 456);
        category1.put("status", "Active");
        category1.put("createdDate", "Jan 1, 2024");
        categories.add(category1);

        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 2L);
        category2.put("name", "House");
        category2.put("description", "Single-family residential homes");
        category2.put("propertyCount", 234);
        category2.put("status", "Active");
        category2.put("createdDate", "Jan 1, 2024");
        categories.add(category2);

        Map<String, Object> category3 = new HashMap<>();
        category3.put("id", 3L);
        category3.put("name", "Condo");
        category3.put("description", "Condominium units");
        category3.put("propertyCount", 189);
        category3.put("status", "Active");
        category3.put("createdDate", "Jan 1, 2024");
        categories.add(category3);

        // Calculate counts
        long activeCount = categories.stream()
                .filter(c -> "Active".equals(c.get("status")))
                .count();
        int totalProperties = categories.stream()
                .mapToInt(c -> (Integer) c.get("propertyCount"))
                .sum();

        model.addAttribute("categories", categories);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("totalProperties", totalProperties);
        return "admin/categories";
    }


    @GetMapping("/reviews")
    public String reviews(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Reviews - Admin Dashboard");

        // Mock review data
        List<Map<String, Object>> reviews = new ArrayList<>();

        Map<String, Object> review1 = new HashMap<>();
        review1.put("id", 1L);
        review1.put("tenantName", "John Doe");
        review1.put("propertyName", "Downtown Apartment");
        review1.put("rating", 4.5);
        review1.put("comment", "Great location and amenities. Highly recommended!");
        review1.put("date", "Jan 15, 2024");
        review1.put("status", "Active");
        review1.put("reports", 0);
        reviews.add(review1);

        Map<String, Object> review2 = new HashMap<>();
        review2.put("id", 2L);
        review2.put("tenantName", "Emily Rodriguez");
        review2.put("propertyName", "Suburban House");
        review2.put("rating", 2.0);
        review2.put("comment", "Property was not as described. Very disappointed.");
        review2.put("date", "Jan 14, 2024");
        review2.put("status", "Flagged");
        review2.put("reports", 3);
        reviews.add(review2);

        // Calculate counts
        long activeCount = reviews.stream()
                .filter(r -> "Active".equals(r.get("status")))
                .count();
        long flaggedCount = reviews.stream()
                .filter(r -> "Flagged".equals(r.get("status")))
                .count();
        double avgRating = reviews.stream()
                .mapToDouble(r -> (Double) r.get("rating"))
                .average()
                .orElse(0.0);

        model.addAttribute("reviews", reviews);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("flaggedCount", flaggedCount);
        model.addAttribute("avgRating", avgRating);
        return "admin/reviews";
    }

    @GetMapping("/landlord-requests")
    public String landlordRequests(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Landlord Requests - Admin Dashboard");

        List<LandlordUpgradeRequest> entityList = requestService.getAllRequests();

        // Chuyển sang List<Map> cho template
        List<Map<String, Object>> requests = new ArrayList<>();
        for (LandlordUpgradeRequest entity : entityList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entity.getId());
            map.put("userName", entity.getUser() != null ? entity.getUser().getName() : "");
            map.put("userEmail", entity.getUser() != null ? entity.getUser().getEmail() : "");
            map.put("requestDate",
                    entity.getCreatedAt() != null
                            ? entity.getCreatedAt()
                            .format(java.time.format.DateTimeFormatter
                                    .ofPattern("dd MMM yyyy HH:mm"))
                            : "");

            map.put("reason", entity.getReason() != null ? entity.getReason() : "");
            String status = entity.getStatus() != null
                    ? entity.getStatus().name().substring(0, 1).toUpperCase()
                    + entity.getStatus().name().substring(1).toLowerCase()
                    : "";
            map.put("status", status);
            requests.add(map);
        }

        long pendingCount = requests.stream().filter(r -> "Pending".equals(r.get("status"))).count();
        long approvedCount = requests.stream().filter(r -> "Approved".equals(r.get("status"))).count();
        long rejectedCount = requests.stream().filter(r -> "Rejected".equals(r.get("status"))).count();

        model.addAttribute("requests", requests);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("approvedCount", approvedCount);
        model.addAttribute("rejectedCount", rejectedCount);
        return "admin/landlord-requests";
    }

    // Xem tất cả request landlord upgrade
    @GetMapping("/landlord-upgrade-requests")
    public String getLandlordUpgradeRequests(
            @RequestParam(required = false) LandlordUpgradeRequestStatus status,
            Model model) {
        List<LandlordUpgradeRequest> requests = (status != null)
                ? requestService.getRequestsByStatus(status)
                : requestService.getRequestsByStatus(LandlordUpgradeRequestStatus.PENDING);
        model.addAttribute("requests", requests);
        return "admin/landlord-requests";
    }

    // Duyệt request
    @PostMapping("/landlord-upgrade-requests/{id}/approve")
    public String approveLandlordRequest(@PathVariable Long id) {
        requestService.approveRequest(id);
        return "redirect:/admin/landlord-requests";
    }

    // Hiển thị form reject
    @GetMapping("/landlord-upgrade-requests/{id}/reject-form")
    public String showRejectForm(@PathVariable Long id, Model model) {
        LandlordUpgradeRequest request = requestService.getRequestById(id);
        if (request == null) {
            return "redirect:/admin/landlord-requests";
        }
        model.addAttribute("request", request);
        model.addAttribute("title", "Reject Landlord Request - Admin Dashboard");
        return "admin/reject-landlord-request";
    }

    // Từ chối request
    @PostMapping("/landlord-upgrade-requests/{id}/reject")
    public String rejectLandlordRequest(@PathVariable Long id, @RequestParam String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return "redirect:/admin/landlord-upgrade-requests/" + id + "/reject-form?error=reason-required";
        }
        requestService.rejectRequest(id, reason.trim());
        return "redirect:/admin/landlord-requests?success=request-rejected";
    }

    @GetMapping("/reports")
    public String reports(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Reports - Admin Dashboard");
        return "admin/reports";
    }

    @GetMapping("/analytics")
    public String analytics(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Analytics - Admin Dashboard");
        return "admin/analytics";
    }

    @GetMapping("/settings")
    public String settings(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Settings - Admin Dashboard");
        return "admin/settings";
    }

    @GetMapping("/logs")
    public String logs(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "System Logs - Admin Dashboard");
        return "admin/logs";
    }
}