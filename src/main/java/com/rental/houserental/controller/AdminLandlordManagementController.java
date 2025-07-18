package com.rental.houserental.controller;

import com.rental.houserental.entity.User;
import com.rental.houserental.entity.Listing;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.ListingService;
import com.rental.houserental.dto.response.listing.ListingResponseDTO;
import com.rental.houserental.dto.request.landlord.LandlordFilterRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/landlords")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLandlordManagementController {
    private final UserService userService;
    private final ListingService listingService;

    @GetMapping("")
    public String landlords(Model model, HttpServletRequest request,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "10") int size,
                           @RequestParam(value = "name", required = false) String name,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "phone", required = false) String phone,
                           @RequestParam(value = "status", required = false) String status,
                           @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
                           @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Landlords - Admin Dashboard");
        LandlordFilterRequestDTO filter = new LandlordFilterRequestDTO();
        filter.setName(name);
        filter.setEmail(email);
        filter.setPhone(phone);
        filter.setStatus(status);
        filter.setSortBy(sortBy);
        filter.setSortDir(sortDir);
        Pageable pageable = PageRequest.of(page, size);
        var landlords = userService.searchLandlords(filter, pageable);
        model.addAttribute("landlords", landlords);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("status", status);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        return "admin/landlords";
    }
} 