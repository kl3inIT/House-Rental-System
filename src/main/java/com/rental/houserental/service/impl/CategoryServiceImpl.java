package com.rental.houserental.service.impl;

import com.rental.houserental.entity.Category;
import com.rental.houserental.exceptions.category.CategoryNotFoundException;
import com.rental.houserental.repository.CategoryRepository;
import com.rental.houserental.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }
}