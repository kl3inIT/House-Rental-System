package com.rental.houserental.controller;

import com.rental.houserental.dto.request.category.CategoryRequestDTO;
import com.rental.houserental.dto.request.category.CategoryUpdateRequestDTO;
import com.rental.houserental.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static com.rental.houserental.constant.ViewNamesConstant.*;
import com.rental.houserental.entity.Category;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    // Hiển thị form thêm mới
    @GetMapping("/add")
    public String showAddCategoryForm(Model model, HttpServletRequest request) {
        model.addAttribute("categoryRequest", new CategoryRequestDTO());
        model.addAttribute("currentUri", request.getRequestURI());
        return CATEGORY_NEW;
    }

    // Xử lý submit form
    @PostMapping("/add")
    public String addCategory(@Valid @ModelAttribute("categoryRequest") CategoryRequestDTO dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUri", request.getRequestURI());
            return CATEGORY_NEW;
        }
        categoryService.addCategory(dto);
        return REDIRECT_CATEGORY;
    }

    @GetMapping("/edit/{id}")
    public String showEditCategoryForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        Category category = categoryService.findById(id);
        CategoryUpdateRequestDTO dto = new CategoryUpdateRequestDTO();
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        model.addAttribute("categoryUpdateRequest", dto);
        model.addAttribute("categoryId", id);
        model.addAttribute("currentUri", request.getRequestURI());
        return "admin/edit-category";
    }

    @PostMapping("/edit/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("categoryUpdateRequest") CategoryUpdateRequestDTO dto,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            model.addAttribute("currentUri", request.getRequestURI());
            return "admin/edit-category";
        }
        categoryService.updateCategory(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Update category successfully!");
        return REDIRECT_CATEGORY;
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Delete category successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Delete failed: " + e.getMessage());
        }
        return REDIRECT_CATEGORY;
    }
} 