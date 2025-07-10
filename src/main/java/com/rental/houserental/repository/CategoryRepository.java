package com.rental.houserental.repository;

import com.rental.houserental.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c.id AS id, c.name AS name, c.description AS description, c.createdAt AS createdAt, c.updatedAt AS updatedAt, COUNT(r.id) AS totalProperties " +
           "FROM Category c LEFT JOIN RentalProperty r ON r.category = c " +
           "GROUP BY c.id, c.name, c.description, c.createdAt, c.updatedAt")
    List<Object[]> findAllWithPropertyCount();

    void deleteById(Long id);
} 