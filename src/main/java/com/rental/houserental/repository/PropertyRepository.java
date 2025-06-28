package com.rental.houserental.repository;

import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.enums.PropertyStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<RentalProperty, Long> {

    @Query("""
            SELECT p FROM RentalProperty p 
            WHERE p.propertyStatus = :status 
            AND EXISTS (SELECT 1 FROM PropertyImage pi WHERE pi.rentalProperty = p)
            ORDER BY p.createdAt DESC
            """)
    List<RentalProperty> findFeaturedProperties(@Param("status") PropertyStatus status, Pageable pageable);
}
