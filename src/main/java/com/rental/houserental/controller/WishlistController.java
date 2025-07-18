package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.WishlistPropertyResponseDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wishlist")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

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

    @PostMapping("/add/{propertyId}")
    public String addToWishlist(
            @PathVariable Long propertyId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/") String returnUrl) {
        User currentUser = userService.findByEmail(principal.getName());
        boolean added = wishlistService.addToWishlist(currentUser.getId(), propertyId);
        if (added) {
            redirectAttributes.addFlashAttribute("successMessage", "Property added to your wishlist");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Property is already in your wishlist");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/remove/{propertyId}")
    public String removeFromWishlist(
            @PathVariable Long propertyId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/wishlist") String returnUrl) {
        User currentUser = userService.findByEmail(principal.getName());
        boolean removed = wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
        if (removed) {
            redirectAttributes.addFlashAttribute("successMessage", "Property removed from your wishlist");
        } else {
            redirectAttributes.addFlashAttribute("warningMessage", "Property was not found in your wishlist");
        }
        return "redirect:" + returnUrl;
    }

    @PostMapping("/toggle/{propertyId}")
    public Object toggleWishlist(
            @PathVariable Long propertyId,
            Principal principal,
            RedirectAttributes redirectAttributes,
            @RequestParam(defaultValue = "/") String returnUrl,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {
        User currentUser = userService.findByEmail(principal.getName());
        boolean isInWishlist = wishlistService.isInWishlist(currentUser.getId(), propertyId);
        boolean added;
        if (isInWishlist) {
            wishlistService.removeFromWishlist(currentUser.getId(), propertyId);
            added = false;
            redirectAttributes.addFlashAttribute("successMessage", "Property removed from your wishlist");
        } else {
            wishlistService.addToWishlist(currentUser.getId(), propertyId);
            added = true;
            redirectAttributes.addFlashAttribute("successMessage", "Property added to your wishlist");
        }
        long wishlistCount = wishlistService.getWishlistCount(currentUser.getId());

        // Nếu là AJAX thì trả về JSON
        if ("XMLHttpRequest".equals(requestedWith)) {
            Map<String, Object> result = new HashMap<>();
            result.put("added", added);
            result.put("wishlistCount", wishlistCount);
            return ResponseEntity.ok(result);
        }
        // Nếu là form thường thì redirect
        return "redirect:" + returnUrl;
    }

    @PostMapping("/clear")
    public String clearWishlist(
            Principal principal,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(principal.getName());
        long countBeforeClear = wishlistService.getWishlistCount(currentUser.getId());
        if (countBeforeClear > 0) {
            wishlistService.clearWishlist(currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Successfully cleared " + countBeforeClear + " properties from your wishlist");
        } else {
            redirectAttributes.addFlashAttribute("infoMessage", "Your wishlist is already empty");
        }
        return "redirect:/wishlist";
    }
} 