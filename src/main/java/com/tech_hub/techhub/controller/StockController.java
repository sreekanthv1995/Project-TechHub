package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.service.products.ProductService;
import com.tech_hub.techhub.service.variant.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class StockController {

    @Autowired
    VariantService variantService;
    @Autowired
    ProductService productService;

    @GetMapping("/stock")
    public String showStock(Model model){
        model.addAttribute("variants",variantService.getAll());
        model.addAttribute("products",productService.getAllProducts());
        return "stock-management";
    }



}
