package com.tech_hub.techhub.service.category;

import com.tech_hub.techhub.entity.Category;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    void save(Category category);
    List<Category> getAll();
    void deleteCategory(Long id);
    Optional<Category> getCategoryById(Long id);
    Optional<Category> findByCategoryByName(String name);
    Page<Category> findAllCategoryByPage(int pageNo, int pageSize, String field, String direction);
    public void updateCategory(Long id,Category category);
}
