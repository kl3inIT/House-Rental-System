package com.rental.houserental.service.impl;

import com.rental.houserental.entity.Category;
import com.rental.houserental.exceptions.category.CategoryNotFoundException;
import com.rental.houserental.repository.CategoryRepository;
import com.rental.houserental.service.CategoryService;
import com.rental.houserental.dto.request.category.CategoryRequestDTO;
import com.rental.houserental.dto.request.category.CategoryUpdateRequestDTO;
import com.rental.houserental.dto.response.category.CategorySummaryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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
    public List<Category> getAllCategories() {
        return findAll(); // Alias for consistency
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
    }

    @Override
    public void addCategory(CategoryRequestDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Long id, CategoryUpdateRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategorySummaryResponseDTO> getCategorySummaries() {
        List<Object[]> results = categoryRepository.findAllWithPropertyCount();
        List<CategorySummaryResponseDTO> summaries = new java.util.ArrayList<>();
        for (Object[] row : results) {
            CategorySummaryResponseDTO dto = new CategorySummaryResponseDTO();
            dto.setId((Long) row[0]);
            dto.setName((String) row[1]);
            dto.setDescription((String) row[2]);
            dto.setCreatedAt((java.time.LocalDateTime) row[3]);
            dto.setUpdatedAt((java.time.LocalDateTime) row[4]);
            dto.setTotalProperties(((Long) row[5]).intValue());
            summaries.add(dto);
        }
        return summaries;
    }

    @Override
    public Page<CategorySummaryResponseDTO> getCategorySummaries(Pageable pageable) {
        List<CategorySummaryResponseDTO> all = getCategorySummaries();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), all.size());
        List<CategorySummaryResponseDTO> pageContent = all.subList(start, end);
        return new PageImpl<>(pageContent, pageable, all.size());
    }

    @Override
    public List<CategorySummaryResponseDTO> getCategorySummariesAll() {
        return getCategorySummaries();
    }
}