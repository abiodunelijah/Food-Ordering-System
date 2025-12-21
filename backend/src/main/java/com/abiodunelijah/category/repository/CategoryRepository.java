package com.abiodunelijah.category.repository;

import com.abiodunelijah.category.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
