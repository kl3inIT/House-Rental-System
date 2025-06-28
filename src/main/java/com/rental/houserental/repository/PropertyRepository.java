package com.rental.houserental.repository;

import com.rental.houserental.entity.RentalProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<RentalProperty, Long> {
}
