package com.tech_hub.techhub.controller;


import com.tech_hub.techhub.dto.ProductDto;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.repository.ProductRepository;
import com.tech_hub.techhub.service.category.CategoryServiceImpl;
import com.tech_hub.techhub.service.products.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class ProductController {

    @Autowired
    CategoryServiceImpl categoryService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductRepository productRepository;
    private static final String ADMIN_PRODUCTS_REDIRECT = "redirect:/admin/products";


    @GetMapping("/page/products/{pageNo}")
    public String viewProduct(@PathVariable (value = "pageNo")int pageNo,
                              @RequestParam(value = "field") String field,
                              @RequestParam(value = "sortDirection") String sortDirection,
                              Model model){
        int pageSize = 5;
        Page<Products> page = productService.findAllByPage(pageNo,pageSize,field,sortDirection);
        List<Products> products = page.getContent();

        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute( "totalItems",page.getTotalElements());
        model.addAttribute("field",field);
        model.addAttribute("sortDirection",sortDirection);
        model.addAttribute("reverseSortDir",sortDirection.equals("asc") ?"desc":"asc");
        model.addAttribute("products",products);
        return "product-management";
    }
    @GetMapping("/products")
    public String showProducts(Model model){
        return viewProduct(1,"name","asc",model);
    }
    @GetMapping("/products/add")
    public String addProduct(Model model){
        model.addAttribute("productDTO",new ProductDto());
        model.addAttribute("categories",categoryService.getAll());
        return "add-product";
    }
    @PostMapping("/products/add")
    public String addProducts(@ModelAttribute("productDTO") ProductDto productDto,
                              @RequestParam("productImage") List<MultipartFile> files) throws IOException {
       productService.addProduct(productDto,files);
        return ADMIN_PRODUCTS_REDIRECT;
    }

    @GetMapping("/products/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String deleteProduct(@PathVariable Long id){
        productService.removeProductById(id);
        return ADMIN_PRODUCTS_REDIRECT;
    }
    @GetMapping("/products/edit/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateProducts(@PathVariable("id") Long id,Model model){

        Products products = productService.getProductById(id).orElseThrow();
       model.addAttribute("product",products);
        return "update-product";
    }
    @PostMapping("/products/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateProduct(@ModelAttribute("product")ProductDto productDto){
        productService.updateProduct(productDto.getId(),productDto);
        return ADMIN_PRODUCTS_REDIRECT;
    }

    @GetMapping("/product/search-product")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String searchProductByName(@RequestParam String searchTerm, Model model) {
        List<Products>product = new ArrayList<>(productRepository.findByName(searchTerm));
        model.addAttribute("products",product);
        return "product-management";
    }
}
