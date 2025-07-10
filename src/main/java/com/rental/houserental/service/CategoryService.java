package com.rental.houserental.service;

import com.rental.houserental.entity.Category;
import com.rental.houserental.dto.request.category.CategoryRequestDTO;
import com.rental.houserental.dto.response.category.CategorySummaryResponseDTO;
import com.rental.houserental.dto.request.category.CategoryUpdateRequestDTO;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();
    
    List<Category> getAllCategories(); // Alias for findAll for consistency
    
    Category findById(Long id);

    void addCategory(CategoryRequestDTO dto);

    List<CategorySummaryResponseDTO> getCategorySummaries();

    void updateCategory(Long id, CategoryUpdateRequestDTO dto);
    void deleteCategory(Long id);
} 