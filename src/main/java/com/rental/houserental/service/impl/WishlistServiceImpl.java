package com.rental.houserental.service.impl;

import com.rental.houserental.dto.response.property.WishlistPropertyResponseDTO;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.repository.PropertyRepository;
import com.rental.houserental.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import com.rental.houserental.exceptions.wishlist.WishlistOperationException;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final PropertyRepository propertyRepository;
    
    private static final String WISHLIST_KEY_PREFIX = "wishlist:user:";
    private static final String PROPERTY_WISHLIST_COUNT_PREFIX = "wishlist:property:";
    private static final String WISHLIST_TIMESTAMP_PREFIX = "wishlist:timestamp:user:";
    
    private String getUserWishlistKey(Long userId) {
        return WISHLIST_KEY_PREFIX + userId;
    }
    
    private String getPropertyWishlistCountKey(Long propertyId) {
        return PROPERTY_WISHLIST_COUNT_PREFIX + propertyId;
    }
    
    private String getUserWishlistTimestampKey(Long userId) {
        return WISHLIST_TIMESTAMP_PREFIX + userId;
    }

    @Override
    public boolean addToWishlist(Long userId, Long propertyId) {
        try {
            String key = getUserWishlistKey(userId);
            String propertyIdStr = propertyId.toString();
            
            // Check if already exists
            Boolean exists = redisTemplate.opsForSet().isMember(key, propertyIdStr);
            if (Boolean.TRUE.equals(exists)) {
                log.debug("Property {} already in wishlist for user {}", propertyId, userId);
                return false;
            }
            
            // Add to user's wishlist
            redisTemplate.opsForSet().add(key, propertyIdStr);
            
            // Store timestamp when added to wishlist
            String timestampKey = getUserWishlistTimestampKey(userId);
            long timestamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            redisTemplate.opsForHash().put(timestampKey, propertyIdStr, String.valueOf(timestamp));
            
            // Increment property wishlist count
            String countKey = getPropertyWishlistCountKey(propertyId);
            redisTemplate.opsForValue().increment(countKey);
            
            log.info("Added property {} to wishlist for user {}", propertyId, userId);
            return true;
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to add property to wishlist", e);
        }
    }

    @Override
    public boolean removeFromWishlist(Long userId, Long propertyId) {
        try {
            String key = getUserWishlistKey(userId);
            String propertyIdStr = propertyId.toString();
            
            // Remove from user's wishlist
            Long removed = redisTemplate.opsForSet().remove(key, propertyIdStr);
            
            if (removed != null && removed > 0) {
                // Remove timestamp
                String timestampKey = getUserWishlistTimestampKey(userId);
                redisTemplate.opsForHash().delete(timestampKey, propertyIdStr);
                
                // Decrement property wishlist count
                String countKey = getPropertyWishlistCountKey(propertyId);
                redisTemplate.opsForValue().decrement(countKey);
                
                log.info("Removed property {} from wishlist for user {}", propertyId, userId);
                return true;
            }
            
            log.debug("Property {} not found in wishlist for user {}", propertyId, userId);
            return false;
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to remove property from wishlist", e);
        }
    }

    @Override
    public boolean isInWishlist(Long userId, Long propertyId) {
        try {
            String key = getUserWishlistKey(userId);
            String propertyIdStr = propertyId.toString();
            
            Boolean exists = redisTemplate.opsForSet().isMember(key, propertyIdStr);
            return Boolean.TRUE.equals(exists);
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to check wishlist status", e);
        }
    }

    @Override
    public Set<String> getWishlistPropertyIds(Long userId) {
        try {
            String key = getUserWishlistKey(userId);
            Set<String> members = redisTemplate.opsForSet().members(key);
            return members != null ? members : new HashSet<>();
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to get wishlist property IDs", e);
        }
    }

    @Override
    public List<WishlistPropertyResponseDTO> getUserWishlist(Long userId) {
        try {
            Set<String> propertyIds = getWishlistPropertyIds(userId);
            
            if (propertyIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Get timestamps
            String timestampKey = getUserWishlistTimestampKey(userId);
            Map<Object, Object> timestamps = redisTemplate.opsForHash().entries(timestampKey);
            
            // Convert to Long IDs
            List<Long> ids = propertyIds.stream()
                    .map(Long::parseLong)
                    .toList();
            
            // Fetch properties from database
            List<RentalProperty> properties = propertyRepository.findAllById(ids);
            
            // Convert to DTOs with timestamps
            return properties.stream()
                    .map(property -> convertToWishlistDTO(property, timestamps))
                    .sorted((a, b) -> b.getAddedToWishlistAt().compareTo(a.getAddedToWishlistAt())) // Sort by newest first
                    .toList();
                    
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to get user wishlist", e);
        }
    }

    @Override
    public long getWishlistCount(Long userId) {
        try {
            String key = getUserWishlistKey(userId);
            Long count = redisTemplate.opsForSet().size(key);
            return count != null ? count : 0;
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to get wishlist count", e);
        }
    }

    @Override
    public void clearWishlist(Long userId) {
        try {
            String key = getUserWishlistKey(userId);
            String timestampKey = getUserWishlistTimestampKey(userId);
            
            // Get all property IDs before clearing
            Set<String> propertyIds = getWishlistPropertyIds(userId);
            
            // Clear user's wishlist and timestamps
            redisTemplate.delete(key);
            redisTemplate.delete(timestampKey);
            
            // Decrement count for each property
            for (String propertyId : propertyIds) {
                String countKey = getPropertyWishlistCountKey(Long.parseLong(propertyId));
                redisTemplate.opsForValue().decrement(countKey);
            }
            
            log.info("Cleared wishlist for user {}", userId);
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to clear wishlist", e);
        }
    }

    @Override
    public Map<Long, Boolean> getWishlistStatus(Long userId, List<Long> propertyIds) {
        try {
            Map<Long, Boolean> statusMap = new HashMap<>();
            String key = getUserWishlistKey(userId);
            
            for (Long propertyId : propertyIds) {
                Boolean exists = redisTemplate.opsForSet().isMember(key, propertyId.toString());
                statusMap.put(propertyId, Boolean.TRUE.equals(exists));
            }
            
            return statusMap;
            
        } catch (Exception e) {
            throw new WishlistOperationException("Failed to get wishlist status", e);
        }
    }
    
    private WishlistPropertyResponseDTO convertToWishlistDTO(RentalProperty property, Map<Object, Object> timestamps) {
        try {
            List<String> imageUrls = property.getImages().stream()
                    .map(pi -> pi.getImageUrl())
                    .collect(Collectors.toList());
            
            List<String> topAmenities = property.getAmenities().stream()
                    .limit(4)
                    .map(amenity -> amenity.getName())
                    .collect(Collectors.toList());
            
            // Get timestamp for this property
            LocalDateTime addedAt = LocalDateTime.now(); // Default to now
            String timestampStr = (String) timestamps.get(property.getId().toString());
            if (timestampStr != null) {
                try {
                    long timestamp = Long.parseLong(timestampStr);
                    addedAt = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
                } catch (NumberFormatException e) {
                    log.warn("Invalid timestamp for property {} in wishlist: {}", property.getId(), timestampStr);
                }
            }
            
            return WishlistPropertyResponseDTO.builder()
                    .id(property.getId())
                    .title(property.getTitle())
                    .monthlyRent(property.getMonthlyRent())
                    .bedrooms(property.getBedrooms())
                    .bathrooms(property.getBathrooms())
                    .area(property.getArea())
                    .fullAddress(property.getFullAddress())
                    .categoryName(property.getCategory() != null ? property.getCategory().getName() : null)
                    .furnishing(property.getFurnishing())
                    .depositPercentage(property.getDepositPercentage())
                    .views(property.getViews())
                    .mainImageUrl(property.getMainImageUrl())
                    .imageUrls(imageUrls)
                    .imageCount(imageUrls.size())
                    .topAmenities(topAmenities)
                    .addedToWishlistAt(addedAt)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error converting property to wishlist DTO: {}", property.getId(), e);
            throw new RuntimeException("Error converting property", e);
        }
    }
} 