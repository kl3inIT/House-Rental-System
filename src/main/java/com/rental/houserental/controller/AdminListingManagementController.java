package com.rental.houserental.controller;

import com.rental.houserental.service.ListingService;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.dto.response.listing.ListingResponseDTO;
import com.rental.houserental.service.UserService;
import com.rental.houserental.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/admin/listings")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminListingManagementController {
    private final ListingService listingService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping("")
    public String listAllListings(
            Model model,
            HttpServletRequest request,
            @RequestParam(value = "landlordName", required = false) String landlordName,
            @RequestParam(value = "landlordId", required = false) Long landlordId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sort", defaultValue = "desc") String sort,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "sortAmount", required = false) String sortAmount,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        if (landlordId != null) {
            User landlord = userService.findById(landlordId);
            if (landlord != null) {
                landlordName = landlord.getName();
            }
        }
        PageRequest pageable = PageRequest.of(page, size);
        var listings = listingService.searchListingsForAdmin(
            landlordName,
            categoryId,
            "desc".equalsIgnoreCase(sort),
            pageable,
            title,
            sortAmount
        );
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "All Listings - Admin Dashboard");
        model.addAttribute("listings", listings.getContent());
        model.addAttribute("totalPages", listings.getTotalPages());
        model.addAttribute("currentPage", listings.getNumber() + 1);
        model.addAttribute("landlordName", landlordName);
        model.addAttribute("landlordId", landlordId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sort", sort);
        model.addAttribute("title", title);
        model.addAttribute("sortAmount", sortAmount);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/all-listings";
    }


} 