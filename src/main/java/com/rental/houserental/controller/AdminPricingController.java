package com.rental.houserental.controller;

import com.rental.houserental.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/pricing")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminPricingController {

    private final SystemConfigService systemConfigService;

    @GetMapping
    public String showPricingConfig(Model model) {
        try {
            BigDecimal normalPriceMonthly = systemConfigService.getNormalListingPricePerMonth();
            BigDecimal highlightPriceMonthly = systemConfigService.getHighlightListingPricePerMonth();
            BigDecimal normalPriceWeekly = systemConfigService.getNormalListingPricePerWeek();
            BigDecimal highlightPriceWeekly = systemConfigService.getHighlightListingPricePerWeek();
            
            model.addAttribute("normalPriceMonthly", normalPriceMonthly);
            model.addAttribute("highlightPriceMonthly", highlightPriceMonthly);
            model.addAttribute("normalPriceWeekly", normalPriceWeekly);
            model.addAttribute("highlightPriceWeekly", highlightPriceWeekly);
            
            return "admin/pricing-config";
        } catch (Exception e) {
            log.error("Error loading pricing configuration", e);
            model.addAttribute("errorMessage", "Failed to load pricing configuration: " + e.getMessage());
            return "admin/pricing-config";
        }
    }

    @PostMapping("/update")
    public String updatePricing(@RequestParam("normalPrice") BigDecimal normalPrice,
                               @RequestParam("highlightPrice") BigDecimal highlightPrice,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validate prices
            if (normalPrice.compareTo(BigDecimal.ZERO) <= 0 || highlightPrice.compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Prices must be greater than 0");
                return "redirect:/admin/pricing";
            }
            
            if (highlightPrice.compareTo(normalPrice) <= 0) {
                redirectAttributes.addFlashAttribute("errorMessage", "Highlight price must be higher than normal price");
                return "redirect:/admin/pricing";
            }
            
            // Update prices
            systemConfigService.setNormalListingPrice(normalPrice);
            systemConfigService.setHighlightListingPrice(highlightPrice);
            
            log.info("Updated pricing - Normal: {}, Highlight: {}", normalPrice, highlightPrice);
            redirectAttributes.addFlashAttribute("successMessage", "Pricing updated successfully!");
            
        } catch (Exception e) {
            log.error("Error updating pricing configuration", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update pricing: " + e.getMessage());
        }
        
        return "redirect:/admin/pricing";
    }
} 