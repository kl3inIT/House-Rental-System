package com.rental.houserental.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryUpdateRequestDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 80, message = "Tên danh mục tối đa 80 ký tự")
    private String name;
    @Size(max = 200, message = "Mô tả tối đa 200 ký tự")
    private String description;
} 