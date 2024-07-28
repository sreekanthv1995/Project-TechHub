package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.dto.ProductDto;
import com.tech_hub.techhub.entity.Category;
import com.tech_hub.techhub.service.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public String getAll(Model model){
        model.addAttribute("categories",categoryService.getAll());
        return "categories";
    }
    @GetMapping("/categories/add")
    public String showCateAddPage(Model model){
        model.addAttribute("category",new Category());
        return "categories-add";
    }

@PostMapping("/categories/add")
public String categoryAdd(@ModelAttribute("category") Category category,
                          @RequestParam String categoryName,Model model) {

        if(categoryName == null || categoryName.isEmpty()){
            model.addAttribute("errorMessage","Category name cannot be empty");
            return "categories-add";
        }
        Optional<Category>optionalCategory = categoryService.findByCategoryByName(categoryName);
        if (optionalCategory.isPresent()){
            model.addAttribute("errorMessage","Category with the same name already exists");
            return "categories-add";
        }else {
            category.setCategoryName(categoryName);
            categoryService.save(category);
        }
        return "redirect:/admin/categories";
}


    @PostMapping("/categories/addModal")
    public String categoryAddModal(Category category,
                              @RequestParam String categoryName,Model model) {
        if(categoryName == null || categoryName.isEmpty()){
            model.addAttribute("errorMessage","Category name cannot be empty");
            return "redirect:/admin/products/add";
        }
        Optional<Category>optionalCategory = categoryService.findByCategoryByName(categoryName);
        if (optionalCategory.isPresent()){
            model.addAttribute("errorMessage","Category with the same name already exists");
            return "redirect:/admin/products/add";
        }else {
            category.setCategoryName(categoryName);
            categoryService.save(category);
        }
        return "redirect:/admin/products/add?message=Category+Added";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable("id") Long id, Model model){
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()){
            model.addAttribute("category",category.get());
            return "categories-update";
        }else {
            return "404";
        }
    }
    @PostMapping("/category/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateProduct(@ModelAttribute("category") Category category){
        categoryService.updateCategory(category.getId(),category);
        return"redirect:/admin/categories";
    }




    @GetMapping("/category/search-category")
    public String searchCategoryByName(@RequestParam String searchTerm, Model model) {
        List<Category> category = categoryService.findByCategoryByName(searchTerm).stream().collect(Collectors.toList());
        model.addAttribute("categories",category);
        return "categories";
    }

}
