package com.rental.houserental.service.impl;

import com.rental.houserental.dto.request.review.ReviewSearchRequestDTO;
import com.rental.houserental.dto.response.review.ReviewResponseDTO;
import com.rental.houserental.entity.Review;
import com.rental.houserental.repository.ReviewRepository;
import com.rental.houserental.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public Page<ReviewResponseDTO> searchReviews(ReviewSearchRequestDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Review> page = reviewRepository.searchReviews(
            emptyToNull(request.getDescription()),
            emptyToNull(request.getTenantName()),
            request.getStar(),
            request.getCategoryId(),
            pageable
        );
        List<ReviewResponseDTO> dtos = page.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    private ReviewResponseDTO toDto(Review review) {
        ReviewResponseDTO dto = new ReviewResponseDTO();
        dto.setId(review.getId());
        dto.setDescription(review.getDescription());
        dto.setStar(review.getStar());
        dto.setTenantName(review.getUser().getName());
        dto.setTenantEmail(review.getUser().getEmail());
        dto.setPropertyId(review.getRentalProperty().getId());
        dto.setPropertyName(review.getRentalProperty().getTitle());
        dto.setCategoryId(review.getRentalProperty().getCategory().getId());
        dto.setCategoryName(review.getRentalProperty().getCategory().getName());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
} 