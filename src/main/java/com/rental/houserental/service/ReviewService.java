package com.rental.houserental.service;

import com.rental.houserental.dto.request.review.ReviewSearchRequestDTO;
import com.rental.houserental.dto.response.review.ReviewResponseDTO;
import org.springframework.data.domain.Page;

public interface ReviewService {
    Page<ReviewResponseDTO> searchReviews(ReviewSearchRequestDTO request);
} 