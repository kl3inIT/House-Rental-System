package com.rental.houserental.service;

import com.rental.houserental.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();
    
    List<Category> getAllCategories(); // Alias for findAll for consistency
    
    Category findById(Long id);
} 