package com.abiodunelijah.category.services;

import com.abiodunelijah.category.dtos.CategoryDto;
import com.abiodunelijah.response.Response;

import java.util.List;

public interface CategoryService {

    Response<CategoryDto> addCategory(CategoryDto categoryDTO);
    Response<CategoryDto> updateCategory(CategoryDto categoryDTO);
    Response<CategoryDto> getCategoryById(Long id);
    Response<List<CategoryDto>> getAllCategories();
    Response<?> deleteCategory(Long id);
}
