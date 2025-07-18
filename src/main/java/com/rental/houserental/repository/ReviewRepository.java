package com.rental.houserental.repository;

import com.rental.houserental.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(value = "SELECT DISTINCT r.* FROM Reviews r " +
           "JOIN Users u ON u.Id = r.UserId " +
           "JOIN RentalProperties p ON p.Id = r.PropertyId " +
           "JOIN Categories c ON c.Id = p.CategoryId " +
           "WHERE (:description IS NULL OR r.Description COLLATE Latin1_General_CI_AI LIKE '%' + :description + '%' COLLATE Latin1_General_CI_AI) " +
           "AND (:tenantName IS NULL OR u.FullName COLLATE Latin1_General_CI_AI LIKE '%' + :tenantName + '%' COLLATE Latin1_General_CI_AI) " +
           "AND (:star IS NULL OR r.Star = :star) " +
           "AND (:categoryId IS NULL OR c.Id = :categoryId) " +
           "ORDER BY r.Id",
           countQuery = "SELECT COUNT(DISTINCT r.Id) FROM Reviews r " +
           "JOIN Users u ON u.Id = r.UserId " +
           "JOIN RentalProperties p ON p.Id = r.PropertyId " +
           "JOIN Categories c ON c.Id = p.CategoryId " +
           "WHERE (:description IS NULL OR r.Description COLLATE Latin1_General_CI_AI LIKE '%' + :description + '%' COLLATE Latin1_General_CI_AI) " +
           "AND (:tenantName IS NULL OR u.FullName COLLATE Latin1_General_CI_AI LIKE '%' + :tenantName + '%' COLLATE Latin1_General_CI_AI) " +
           "AND (:star IS NULL OR r.Star = :star) " +
           "AND (:categoryId IS NULL OR c.Id = :categoryId)",
           nativeQuery = true)
    Page<Review> searchReviews(
        @Param("description") String description,
        @Param("tenantName") String tenantName,
        @Param("star") Integer star,
        @Param("categoryId") Long categoryId,
        Pageable pageable
    );
} 