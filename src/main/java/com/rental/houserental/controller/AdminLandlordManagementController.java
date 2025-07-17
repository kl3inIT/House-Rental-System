package com.rental.houserental.controller;

import com.rental.houserental.entity.User;
import com.rental.houserental.entity.Listing;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.ListingService;
import com.rental.houserental.dto.response.listing.ListingResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/landlords")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLandlordManagementController {
    private final UserService userService;
    private final ListingService listingService;

    @GetMapping("")
    public String landlords(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Landlords - Admin Dashboard");
        List<User> landlords = userService.getAllLandlords();
        model.addAttribute("landlords", landlords);
        return "admin/landlords";
    }
} 