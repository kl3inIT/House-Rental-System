package com.rental.houserental.service;

import com.rental.houserental.dto.response.property.WishlistPropertyResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WishlistService {
    
    /**
     * Add a property to user's wishlist
     * @param userId User ID
     * @param propertyId Property ID
     * @return true if added successfully, false if already exists
     */
    boolean addToWishlist(Long userId, Long propertyId);
    
    /**
     * Remove a property from user's wishlist
     * @param userId User ID
     * @param propertyId Property ID
     * @return true if removed successfully, false if not found
     */
    boolean removeFromWishlist(Long userId, Long propertyId);
    
    /**
     * Check if a property is in user's wishlist
     * @param userId User ID
     * @param propertyId Property ID
     * @return true if property is in wishlist
     */
    boolean isInWishlist(Long userId, Long propertyId);
    
    /**
     * Get all property IDs in user's wishlist
     * @param userId User ID
     * @return Set of property IDs
     */
    Set<String> getWishlistPropertyIds(Long userId);
    
    /**
     * Get user's wishlist with full property details
     * @param userId User ID
     * @return List of WishlistPropertyResponseDTO
     */
    List<WishlistPropertyResponseDTO> getUserWishlist(Long userId);
    
    /**
     * Get wishlist count for a user
     * @param userId User ID
     * @return number of properties in wishlist
     */
    long getWishlistCount(Long userId);
    
    /**
     * Clear all properties from user's wishlist
     * @param userId User ID
     */
    void clearWishlist(Long userId);
    
    /**
     * Get wishlist for multiple properties (for checking status on listing pages)
     * @param userId User ID
     * @param propertyIds List of property IDs to check
     * @return Map of propertyId -> isInWishlist
     */
    Map<Long, Boolean> getWishlistStatus(Long userId, List<Long> propertyIds);
} 