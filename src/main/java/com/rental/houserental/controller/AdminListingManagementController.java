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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "landlordName", required = false) String landlordName,
            @RequestParam(value = "landlordId", required = false) Long landlordId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "sort", defaultValue = "desc") String sort,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "sortAmount", required = false) String sortAmount
    ) {
        // Nếu có landlordId thì lấy tên landlord từ DB
        if (landlordId != null) {
            User landlord = userService.findById(landlordId);
            if (landlord != null) {
                landlordName = landlord.getName();
            }
        }
        boolean newestFirst = !"asc".equalsIgnoreCase(sort);
        Pageable pageable = PageRequest.of(page, size);
        Page<ListingResponseDTO> listings = listingService.searchListingsForAdmin(landlordName, categoryId, newestFirst, pageable, title, sortAmount);
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "All Listings - Admin Dashboard");
        model.addAttribute("listings", listings);
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