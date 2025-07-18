package com.rental.houserental.controller;

import com.rental.houserental.dto.request.review.ReviewSearchRequestDTO;
import com.rental.houserental.dto.response.review.ReviewResponseDTO;
import com.rental.houserental.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("")
    public String listReviews(
        @RequestParam(value = "description", required = false) String description,
        @RequestParam(value = "tenantName", required = false) String tenantName,
        @RequestParam(value = "star", required = false) Integer star,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model
    ) {
        ReviewSearchRequestDTO req = new ReviewSearchRequestDTO();
        req.setDescription(description);
        req.setTenantName(tenantName);
        req.setStar(star);
        req.setCategoryId(categoryId);
        req.setPage(page);
        req.setSize(size);
        Page<ReviewResponseDTO> reviews = reviewService.searchReviews(req);
        model.addAttribute("reviews", reviews.getContent());
        model.addAttribute("totalPages", reviews.getTotalPages());
        model.addAttribute("currentPage", reviews.getNumber());
        model.addAttribute("totalElements", reviews.getTotalElements());
        // Truyền lại filter để giữ giá trị trên form
        model.addAttribute("description", description);
        model.addAttribute("tenantName", tenantName);
        model.addAttribute("star", star);
        model.addAttribute("categoryId", categoryId);
        return "admin/reviews";
    }
} 