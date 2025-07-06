package com.rental.houserental.repository;

import com.rental.houserental.entity.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<PropertyImage, Long> {
    
    List<PropertyImage> findByRentalPropertyIdOrderByDisplayOrderAscCreatedAtAsc(Long propertyId);
    
    List<PropertyImage> findByRentalPropertyIdAndIsMainImageTrue(Long propertyId);
    
    @Query("SELECT MAX(i.displayOrder) FROM PropertyImage i WHERE i.rentalProperty.id = :propertyId")
    Integer findMaxDisplayOrderByPropertyId(@Param("propertyId") Long propertyId);
    
    void deleteByRentalPropertyId(Long propertyId);
} 