package com.rental.houserental.advice;

import com.rental.houserental.entity.User;
import com.rental.houserental.service.UserService;
import com.rental.houserental.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class WishlistControllerAdvice {

    private final WishlistService wishlistService;
    private final UserService userService;

    @ModelAttribute("wishlistCount")
    public Long getWishlistCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
                
                User currentUser = userService.findByEmail(authentication.getName());
                if (currentUser != null) {
                    return wishlistService.getWishlistCount(currentUser.getId());
                }
            }
        } catch (Exception e) {
            log.debug("Error getting wishlist count for global context: {}", e.getMessage());
        }
        
        return 0L;
    }
} 