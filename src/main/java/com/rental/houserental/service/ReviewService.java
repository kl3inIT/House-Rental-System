package com.rental.houserental.service;

import com.rental.houserental.dto.request.review.ReviewSearchRequestDTO;
import com.rental.houserental.dto.response.review.ReviewResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ReviewService {
    Page<ReviewResponseDTO> searchReviews(ReviewSearchRequestDTO request);
    List<ReviewResponseDTO> getReviewsByPropertyId(Long propertyId);
    void addReview(Long userId, com.rental.houserental.dto.request.review.ReviewRequestDTO request);
    boolean hasUserReviewedProperty(Long userId, Long propertyId);
    void deleteReviewByUser(Long reviewId, Long userId);
} 