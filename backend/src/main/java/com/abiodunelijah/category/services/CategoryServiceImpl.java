package com.abiodunelijah.category.services;


import com.abiodunelijah.category.dtos.CategoryDto;
import com.abiodunelijah.category.entities.Category;
import com.abiodunelijah.category.repository.CategoryRepository;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;



    @Override
    public Response<CategoryDto> addCategory(CategoryDto categoryDTO) {

        log.info("Inside the addCategory()");

        Category category = modelMapper.map(categoryDTO, Category.class);
        categoryRepository.save(category);

        return Response.<CategoryDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category added successfully.")
                .build();
    }

    @Override
    public Response<CategoryDto> updateCategory(CategoryDto categoryDTO) {

        log.info("Inside updateCategory()");

        Category category = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new NotFoundException("Category not found."));

        if (categoryDTO.getName() != null && !categoryDTO.getName().isEmpty()){
            category.setName(categoryDTO.getName());
        }

        if (categoryDTO.getDescription()  != null){
            category.setDescription(categoryDTO.getDescription());
        }

        categoryRepository.save(category);

        return Response.<CategoryDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category updated successfully.")
                .build();
    }

    @Override
    public Response<CategoryDto> getCategoryById(Long id) {

        log.info("Inside getCategoryById()");

        Category category = categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Category not found."));

        CategoryDto categoryDTO = modelMapper.map(category, CategoryDto.class);

        return Response.<CategoryDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category deleted successfully.")
                .data(categoryDTO)
                .build();
    }

    @Override
    public Response<List<CategoryDto>> getAllCategories() {

        log.info("Inside getAllCategories()");

        List<Category> categories = categoryRepository.findAll();

        List<CategoryDto> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();

        return Response.<List<CategoryDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All categories retrieved successfully.")
                .data(categoryDTOS)
                .build();
    }

    @Override
    public Response<?> deleteCategory(Long id) {

        log.info("Inside deletedCategory()");

        if (!categoryRepository.existsById(id)){
            throw new NotFoundException("Category not found.");
        }

        categoryRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Category deleted successfully.")
                .build();
    }
}
