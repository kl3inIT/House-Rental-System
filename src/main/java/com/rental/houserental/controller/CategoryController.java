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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import static com.rental.houserental.constant.ViewNamesConstant.*;
import com.rental.houserental.entity.Category;
import com.rental.houserental.dto.response.category.CategorySummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("contains properties")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete category because it contains properties.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Delete failed: " + e.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Delete failed: " + e.getMessage());
        }
        return REDIRECT_CATEGORY;
    }

    @GetMapping("")
    public String listCategories(Model model, HttpServletRequest request,
                                 @RequestParam(value = "page", defaultValue = "0") int page,
                                 @RequestParam(value = "size", defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategorySummaryResponseDTO> categories = categoryService.getCategorySummaries(pageable);

        // Lấy toàn bộ category để tính tổng property
        long totalProperties = categoryService.getCategorySummariesAll()
            .stream().mapToInt(CategorySummaryResponseDTO::getTotalProperties).sum();

        model.addAttribute("categories", categories);
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("title", "Manage Categories - Admin Dashboard");
        model.addAttribute("totalCategories", categories.getTotalElements());
        model.addAttribute("totalProperties", totalProperties);
        model.addAttribute("newThisMonth", 0); // Tùy chỉnh nếu cần
        return "admin/categories";
    }
} 