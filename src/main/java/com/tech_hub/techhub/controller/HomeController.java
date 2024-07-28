package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.Offer;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Category;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.service.category.CategoryService;
import com.tech_hub.techhub.service.products.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;


    @GetMapping(value = {"/", "/index"})
    public String showHome(Model model) {
        List<Products> allProducts = productService.getAllProducts();

        List<Products> latestProducts = allProducts.stream().sorted(Comparator.comparing(Products::getCreatedAt).reversed())
                .limit(8).toList();
        model.addAttribute("latestProducts", latestProducts);
        return "index";
    }


    @GetMapping("/shopPage/viewProduct/{id}")
    public String viewProduct(@PathVariable("id") Long id, Model model) {
        Optional<Products> optionalProduct = productService.getProductById(id);

        if (optionalProduct.isPresent()) {
            Products product = optionalProduct.get();
            List<Variant> variants = productService.getVariantForProduct(product);

            model.addAttribute("product", product);
            model.addAttribute("variants", variants);
            return "single-product";
        } else {
            return "404";
        }
    }

    @GetMapping("/shopPage")
    public String showProduct(
            @RequestParam(value = "itemsInPage",defaultValue = "8")int itemPerPage,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "field", defaultValue = "name") String field,
            @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
            Model model) {

        Page<Products> page = productService.findAllByPage(pageNo, itemPerPage, field, sortDirection);
        List<Products> products = page.getContent();

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("field", field);
        model.addAttribute("sortDirection", sortDirection);
        model.addAttribute("reverseSortDir", sortDirection.equals("asc") ? "desc" : "asc");
        model.addAttribute("totalItemsPerPage",page.getNumberOfElements());

        return "products";
    }


    @GetMapping("/shopPage/category/{id}")
    public String shopByCategory(@PathVariable Long id,
                                    @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                                    @RequestParam(value = "field", defaultValue = "name") String field,
                                    @RequestParam(value = "sortDirection", defaultValue = "asc") String sortDirection,
                                    Model model) {

        int pageSize = 4;

        List<Category> categories = categoryService.getAll();
        Optional<Category> selectedCategoryOptional = categoryService.getCategoryById(id);

        if (selectedCategoryOptional.isPresent()) {
            Category selectedCategory = selectedCategoryOptional.get();
            Page<Products> page = productService.findProductsByCategoryIdAndPage(id, pageNo, pageSize, field, sortDirection);
            List<Products> products = page.getContent();

            model.addAttribute("categories", categories);
            model.addAttribute("selectedCategory", selectedCategory);
            model.addAttribute("products", products);
            model.addAttribute("currentPage", pageNo);
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("totalItems", page.getTotalElements());
            model.addAttribute("field", field);
            model.addAttribute("sortDirection", sortDirection);
            model.addAttribute("reverseSortDir", sortDirection.equals("asc") ? "desc" : "asc");

            return "products";
        } else {
            return "404";
        }
    }

    @GetMapping("/offer/products")
    public String showOfferProducts(Model model) {
        List<Products> allProducts = productService.getAllProducts();
        List<Variant> offerVariants = productService.getVariantForProduct((Products) allProducts).stream()
                .filter(variant -> variant.getDiscount().isEnabled()).toList();
        model.addAttribute("offerVariants", offerVariants);
        return "index";
    }
}
