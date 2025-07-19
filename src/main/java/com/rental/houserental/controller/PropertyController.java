package com.rental.houserental.controller;

import com.rental.houserental.dto.response.property.PropertyDetailDTO;
import com.rental.houserental.entity.User;
import com.rental.houserental.service.PropertyService;
import com.rental.houserental.service.ReviewService;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.rental.houserental.dto.request.review.ReviewRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

import static com.rental.houserental.constant.ViewNamesConstant.PROPERTY_DETAIL;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final UserService userService;
    private final WishlistService wishlistService;
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public String viewPropertyDetail(@PathVariable Long id, Model model, Principal principal) {
        PropertyDetailDTO property = propertyService.getPropertyDetailById(id);
        Long currentUserId = null;
        boolean isInWishlist = false;

        if (principal != null) {
            User currentUser = userService.findByEmail(principal.getName());
            currentUserId = currentUser.getId();
            // Check if property is in user's wishlist
            isInWishlist = wishlistService.isInWishlist(currentUserId, id);

            if (currentUserId != null && !currentUserId.equals(property.getLandlordId())) {
                propertyService.increaseView(id);
            }
        }

        model.addAttribute("property", property);
        model.addAttribute("similarProperties", propertyService.getSimularProperties(3, id));
        model.addAttribute("isInWishlist", isInWishlist);
        model.addAttribute("currentUserEmail", principal.getName());
        if (currentUserId != null && !currentUserId.equals(property.getLandlordId())) {
            propertyService.increaseView(id);
        }
        boolean hasReviewed = false;
        if (currentUserId != null) {
            hasReviewed = reviewService.hasUserReviewedProperty(currentUserId, id);
        }
        model.addAttribute("hasReviewed", hasReviewed);
        model.addAttribute("property", property);
        model.addAttribute("similarProperties", propertyService.getSimularProperties(3, id));
        model.addAttribute("reviews", reviewService.getReviewsByPropertyId(id));
        return PROPERTY_DETAIL;
    }

    @PostMapping("/{id}/reviews")
    public String addReview(@PathVariable Long id,
                           @Valid @ModelAttribute ReviewRequestDTO reviewRequestDTO,
                           BindingResult bindingResult,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to review.");
            return "redirect:/login";
        }
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldError("description") != null
                    ? bindingResult.getFieldError("description").getDefaultMessage()
                    : "Invalid input!";
            redirectAttributes.addFlashAttribute("error", errorMsg);
            return "redirect:/properties/" + id + "#reviews-content";
        }
        User user = userService.findByEmail(principal.getName());
        reviewRequestDTO.setPropertyId(id);
        if (reviewService.hasUserReviewedProperty(user.getId(), id)) {
            redirectAttributes.addFlashAttribute("error", "You have already reviewed this property.");
            return "redirect:/properties/" + id + "#reviews-content";
        }
        reviewService.addReview(user.getId(), reviewRequestDTO);
        redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
        return "redirect:/properties/" + id + "#reviews-content";
    }

    @PostMapping("/{propertyId}/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long propertyId,
                              @PathVariable Long reviewId,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("error", "You must be logged in to delete your review.");
            return "redirect:/login";
        }
        User user = userService.findByEmail(principal.getName());
        reviewService.deleteReviewByUser(reviewId, user.getId());
        redirectAttributes.addFlashAttribute("success", "Review deleted successfully!");
        return "redirect:/properties/" + propertyId + "#reviews-content";
    }
}
