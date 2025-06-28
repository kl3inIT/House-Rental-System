package com.rental.houserental.repository;

import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.enums.PropertyStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<RentalProperty, Long> {
    
    // Find featured properties (AVAILABLE status, limit by Pageable)
    List<RentalProperty> findByPropertyStatusOrderByCreatedAtDesc(PropertyStatus status, Pageable pageable);
    
    // Count available properties
    Long countByPropertyStatus(PropertyStatus status);
    
    // Find properties with images - for better featured display
    @Query("SELECT p FROM RentalProperty p LEFT JOIN FETCH p.images WHERE p.propertyStatus = :status ORDER BY p.createdAt DESC")
    List<RentalProperty> findByPropertyStatusWithImages(PropertyStatus status, Pageable pageable);
}
