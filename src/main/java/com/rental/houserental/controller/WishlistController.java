package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.WishlistPropertyResponseDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    /**
     * Display user's wishlist page
     */
    @GetMapping
    public String wishlistPage(Model model, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        List<WishlistPropertyResponseDTO> wishlistProperties = wishlistService.getUserWishlist(currentUser.getId());
        long wishlistCount = wishlistService.getWishlistCount(currentUser.getId());
        
        model.addAttribute("wishlistProperties", wishlistProperties);
        model.addAttribute("wishlistCount", wishlistCount);
        model.addAttribute("title", "My Wishlist - RentEase");
        
        return "user/wishlist";
    }

    /**
     * Add property to wishlist
     */
    @PostMapping("/add/{propertyId}")
    public String addToWishlist(
            @PathVariable Long propertyId, 
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/") String returnUrl) {
        
        try {
            User currentUser = userService.findByEmail(principal.getName());
            boolean added = wishlistService.addToWishlist(currentUser.getId(), propertyId);
            
            if (added) {
                redirectAttributes.addFlashAttribute("successMessage", "Property added to your wishlist");
                log.info("User {} added property {} to wishlist", currentUser.getId(), propertyId);
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Property is already in your wishlist");
            }
            
        } catch (Exception e) {
            log.error("Error adding property {} to wishlist", propertyId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add property to wishlist");
        }
        
        return "redirect:" + returnUrl;
    }

    /**
     * Remove property from wishlist
     */
    @PostMapping("/remove/{propertyId}")
    public String removeFromWishlist(
            @PathVariable Long propertyId, 
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/wishlist") String returnUrl) {
        
        try {
            User currentUser = userService.findByEmail(principal.getName());
            boolean removed = wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
            
            if (removed) {
                redirectAttributes.addFlashAttribute("successMessage", "Property removed from your wishlist");
                log.info("User {} removed property {} from wishlist", currentUser.getId(), propertyId);
            } else {
                redirectAttributes.addFlashAttribute("warningMessage", "Property was not found in your wishlist");
            }
            
        } catch (Exception e) {
            log.error("Error removing property {} from wishlist", propertyId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove property from wishlist");
        }
        
        return "redirect:" + returnUrl;
    }

    /**
     * Toggle wishlist status (for quick actions)
     */
    @PostMapping("/toggle/{propertyId}")
    public String toggleWishlist(
            @PathVariable Long propertyId, 
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/") String returnUrl) {
        
        try {
            User currentUser = userService.findByEmail(principal.getName());
            boolean isInWishlist = wishlistService.isInWishlist(currentUser.getId(), propertyId);
            
            if (isInWishlist) {
                // Remove from wishlist
                boolean removed = wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
                if (removed) {
                    redirectAttributes.addFlashAttribute("successMessage", "Property removed from your wishlist");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove property from wishlist");
                }
            } else {
                // Add to wishlist
                boolean added = wishlistService.addToWishlist(currentUser.getId(), propertyId);
                if (added) {
                    redirectAttributes.addFlashAttribute("successMessage", "Property added to your wishlist");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to add property to wishlist");
                }
            }
            
        } catch (Exception e) {
            log.error("Error toggling wishlist for property {}", propertyId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update wishlist");
        }
        
        return "redirect:" + returnUrl;
    }

    /**
     * Clear entire wishlist
     */
    @PostMapping("/clear")
    public String clearWishlist(
            Principal principal,
            RedirectAttributes redirectAttributes) {
        
        try {
            User currentUser = userService.findByEmail(principal.getName());
            long countBeforeClear = wishlistService.getWishlistCount(currentUser.getId());
            
            if (countBeforeClear > 0) {
                wishlistService.clearWishlist(currentUser.getId());
                redirectAttributes.addFlashAttribute("successMessage", 
                    "Successfully cleared " + countBeforeClear + " properties from your wishlist");
                log.info("User {} cleared their wishlist ({} properties)", currentUser.getId(), countBeforeClear);
            } else {
                redirectAttributes.addFlashAttribute("infoMessage", "Your wishlist is already empty");
            }
            
        } catch (Exception e) {
            log.error("Error clearing wishlist", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear wishlist");
        }
        
        return "redirect:/wishlist";
    }
} 