package com.rental.houserental.controller;

import com.rental.houserental.dto.request.property.CreatePropertyRequestDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.enums.FurnishingType;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
import static com.rental.houserental.constant.AtrributeNameConstant.CATEGORIES;
import static com.rental.houserental.constant.AtrributeNameConstant.PROPERTY_REQUEST;
import static com.rental.houserental.constant.AtrributeNameConstant.SUCCESS_MESSAGE;
import static com.rental.houserental.constant.ViewNamesConstant.*;

@Controller
@RequestMapping("/landlord/properties")
@PreAuthorize("hasRole('LANDLORD')")
@RequiredArgsConstructor
@Slf4j
public class LandlordPropertiesController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/new")
    public String newPropertyForm(Model model) {
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        return LANDLORD_NEW_LISTING;
    }

    @PostMapping()
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
        return REDIRECT_LANDLORD_NEW_LISTING;
    }

    @GetMapping()
    public String allProperties(Model model) {
        model.addAttribute(PROPERTY_REQUEST, new CreatePropertyRequestDTO());
        model.addAttribute(CATEGORIES, categoryService.findAll());
        List<Map<String, String>> types = FurnishingType.getTypeList();
        model.addAttribute(FURNISHING_TYPES, types);
        return LANDLORD_PROPERTIES;
    }

    @GetMapping("/properties")
    public String properties(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Mock data for properties list
        List<Map<String, Object>> properties = new ArrayList<>();

        Map<String, Object> property1 = new HashMap<>();
        property1.put("id", 1L);
        property1.put("title", "Modern Downtown Apartment");
        property1.put("address", "123 Main St, Downtown, New York, NY 10001");
        property1.put("monthlyRent", new BigDecimal("1800.00"));
        property1.put("status", "OCCUPIED");
        property1.put("bedrooms", 2);
        property1.put("bathrooms", 2);
        property1.put("area", 1200);
        property1.put("views", 45);
        property1.put("inquiries", 8);
        property1.put("occupancy", "100%");
        property1.put("rating", 4.8);
        properties.add(property1);

        Map<String, Object> property2 = new HashMap<>();
        property2.put("id", 2L);
        property2.put("title", "Suburban Family House");
        property2.put("address", "456 Oak Ave, Suburbs, New York, NY 10025");
        property2.put("monthlyRent", new BigDecimal("2200.00"));
        property2.put("status", "AVAILABLE");
        property2.put("bedrooms", 3);
        property2.put("bathrooms", 2);
        property2.put("area", 1800);
        property2.put("views", 23);
        property2.put("inquiries", 3);
        property2.put("occupancy", "0%");
        property2.put("rating", null);
        properties.add(property2);

        Map<String, Object> property3 = new HashMap<>();
        property3.put("id", 3L);
        property3.put("title", "Cozy Studio Apartment");
        property3.put("address", "789 Pine St, Midtown, New York, NY 10018");
        property3.put("monthlyRent", new BigDecimal("1200.00"));
        property3.put("status", "OCCUPIED");
        property3.put("bedrooms", 0);
        property3.put("bathrooms", 1);
        property3.put("area", 600);
        property3.put("views", 31);
        property3.put("inquiries", 5);
        property3.put("occupancy", "100%");
        property3.put("rating", 4.6);
        properties.add(property3);

        model.addAttribute("properties", properties);
        return "landlord/properties";
    }


    @GetMapping("/requests")
    public String requests(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Mock data for rental requests
        List<Map<String, Object>> requests = new ArrayList<>();

        Map<String, Object> request1 = new HashMap<>();
        request1.put("id", 1L);
        request1.put("tenantName", "Michael Chen");
        request1.put("tenantEmail", "michael.chen@email.com");
        request1.put("propertyName", "Downtown Apartment - Unit 4B");
        request1.put("message", "Interested in viewing the property this weekend. Available Saturday afternoon.");
        request1.put("status", "PENDING");
        request1.put("requestDate", "2025-01-15");
        requests.add(request1);

        Map<String, Object> request2 = new HashMap<>();
        request2.put("id", 2L);
        request2.put("tenantName", "Emily Rodriguez");
        request2.put("tenantEmail", "emily.rodriguez@email.com");
        request2.put("propertyName", "Suburban House - 123 Oak St");
        request2.put("message", "Looking to move in next month. Has excellent references and stable income.");
        request2.put("status", "PENDING");
        request2.put("requestDate", "2025-01-14");
        requests.add(request2);

        Map<String, Object> request3 = new HashMap<>();
        request3.put("id", 3L);
        request3.put("tenantName", "David Kim");
        request3.put("tenantEmail", "david.kim@email.com");
        request3.put("propertyName", "Studio Apartment - Unit 2A");
        request3.put("message", "Application approved. Lease signing scheduled for tomorrow.");
        request3.put("status", "APPROVED");
        request3.put("requestDate", "2025-01-13");
        requests.add(request3);

        model.addAttribute("requests", requests);
        return "landlord/requests";
    }

    @GetMapping("/bookings")
    public String bookings(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Mock data for bookings
        List<Map<String, Object>> bookings = new ArrayList<>();

        Map<String, Object> booking1 = new HashMap<>();
        booking1.put("id", 1L);
        booking1.put("tenantName", "Sarah Wilson");
        booking1.put("propertyName", "Downtown Apartment - Unit 4B");
        booking1.put("startDate", "2025-02-01");
        booking1.put("endDate", "2025-07-31");
        booking1.put("monthlyRent", new BigDecimal("1800.00"));
        booking1.put("status", "ACTIVE");
        bookings.add(booking1);

        Map<String, Object> booking2 = new HashMap<>();
        booking2.put("id", 2L);
        booking2.put("tenantName", "John Smith");
        booking2.put("propertyName", "Studio Apartment - Unit 2A");
        booking2.put("startDate", "2025-01-15");
        booking2.put("endDate", "2025-06-14");
        booking2.put("monthlyRent", new BigDecimal("1200.00"));
        booking2.put("status", "ACTIVE");
        bookings.add(booking2);

        model.addAttribute("bookings", bookings);
        return "landlord/bookings";
    }



    @GetMapping("/messages")
    public String messages(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        // Mock messages data
        List<Map<String, Object>> messages = new ArrayList<>();

        Map<String, Object> message1 = new HashMap<>();
        message1.put("id", 1L);
        message1.put("from", "Michael Chen");
        message1.put("subject", "Property Viewing Request");
        message1.put("preview", "Hi, I'm interested in viewing your downtown apartment...");
        message1.put("date", "2025-01-15");
        message1.put("isRead", false);
        messages.add(message1);

        Map<String, Object> message2 = new HashMap<>();
        message2.put("id", 2L);
        message2.put("from", "Emily Rodriguez");
        message2.put("subject", "Application Status");
        message2.put("preview", "Thank you for considering my application...");
        message2.put("date", "2025-01-14");
        message2.put("isRead", true);
        messages.add(message2);

        model.addAttribute("messages", messages);
        return "landlord/messages";
    }

    @GetMapping("/settings")
    public String settings(Model model, Authentication authentication, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        User landlord = userService.findByEmail(authentication.getName());
        model.addAttribute("user", landlord);
        return "landlord/settings";
    }
}
