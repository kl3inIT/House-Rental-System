package com.rental.houserental.controller;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rental.houserental.constant.AtrributeNameConstant.*;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequestMapping("/landlord")
@PreAuthorize("hasRole('LANDLORD')")
@RequiredArgsConstructor
@Slf4j
public class LandlordDashboardController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Dashboard Statistics - using hardcoded values for now
        Map<String, Object> dashboardStats = new HashMap<>();
        dashboardStats.put("totalProperties", 12);
        dashboardStats.put("occupiedProperties", 9);
        dashboardStats.put("pendingRequests", 5);
        dashboardStats.put("monthlyRevenue", new BigDecimal("18400.00"));

        // Recent Requests (mock data for now)
        List<Map<String, Object>> recentRequests = new ArrayList<>();

        Map<String, Object> request1 = new HashMap<>();
        request1.put("tenantName", "Michael Chen");
        request1.put("propertyName", "Downtown Apartment - Unit 4B");
        request1.put("message", "Interested in viewing the property this weekend. Available Saturday afternoon.");
        request1.put("status", "PENDING");
        recentRequests.add(request1);

        Map<String, Object> request2 = new HashMap<>();
        request2.put("tenantName", "Emily Rodriguez");
        request2.put("propertyName", "Suburban House - 123 Oak St");
        request2.put("message", "Looking to move in next month. Has excellent references and stable income.");
        request2.put("status", "PENDING");
        recentRequests.add(request2);

        Map<String, Object> request3 = new HashMap<>();
        request3.put("tenantName", "David Kim");
        request3.put("propertyName", "Studio Apartment - Unit 2A");
        request3.put("message", "Application approved. Lease signing scheduled for tomorrow.");
        request3.put("status", "APPROVED");
        recentRequests.add(request3);

        // Property Performance (mock data for now)
        List<Map<String, Object>> propertyPerformance = new ArrayList<>();

        Map<String, Object> property1 = new HashMap<>();
        property1.put("name", "Downtown Apartment");
        property1.put("address", "123 Main St, Unit 4B");
        property1.put("rent", new BigDecimal("1800.00"));
        property1.put("isOccupied", true);
        property1.put("monthlyViews", 45);
        property1.put("interestLevel", 100);
        propertyPerformance.add(property1);

        Map<String, Object> property2 = new HashMap<>();
        property2.put("name", "Suburban House");
        property2.put("address", "456 Oak Ave");
        property2.put("rent", new BigDecimal("2200.00"));
        property2.put("isOccupied", false);
        property2.put("monthlyViews", 23);
        property2.put("interestLevel", 60);
        propertyPerformance.add(property2);

        Map<String, Object> property3 = new HashMap<>();
        property3.put("name", "Studio Apartment");
        property3.put("address", "789 Pine St, Unit 2A");
        property3.put("rent", new BigDecimal("1200.00"));
        property3.put("isOccupied", true);
        property3.put("monthlyViews", 31);
        property3.put("interestLevel", 100);
        propertyPerformance.add(property3);

        model.addAttribute("dashboardStats", dashboardStats);
        model.addAttribute("recentRequests", recentRequests);
        model.addAttribute("propertyPerformance", propertyPerformance);

        return LANDLORD_DASHBOARD;
    }

    @GetMapping("/properties/new")
    public String newPropertyForm(Model model) {
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        return LANDLORD_NEW_LISTING;
    }

    @PostMapping("/properties")
    public String createProperty(@Valid @ModelAttribute(PROPERTY_REQUEST) CreatePropertyRequestDTO request,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "imageFiles", required = false) MultipartFile[] imageFiles,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(PROPERTY_REQUEST, request);
            model.addAttribute(CATEGORIES, categoryService.findAll());
            return LANDLORD_NEW_LISTING;
        }

        User landlord = userService.findByEmail(authentication.getName());

        propertyService.createProperty(request, landlord, imageFiles);

        redirectAttributes.addFlashAttribute(SUCCESS_MESSAGE, "Property listing created successfully and saved as draft.");
        return LANDLORD_NEW_LISTING;
    }
}