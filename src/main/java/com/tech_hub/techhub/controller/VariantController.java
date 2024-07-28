package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.repository.VariantRepository;
import com.tech_hub.techhub.service.products.ProductServiceImpl;
import com.tech_hub.techhub.service.variant.VariantServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/variant")
public class VariantController {

    @Autowired
    VariantServiceImpl variantService;
    @Autowired
    VariantRepository variantRepository;
    @Autowired
    ProductServiceImpl productService;


    @GetMapping("/variantList")
    public String showAllVariant(Model model) {
        model.addAttribute("variants", variantService.getAll());
        return "variant-management";
    }

    @GetMapping("/addVariant")
    public String showVariantAddPage(Model model) {
        List<Products> product = productService.getAllProducts();
        model.addAttribute("product", product);
        model.addAttribute("variants", new Variant());
        return "variant-add";
    }

    @PostMapping("/saveVariant")
    public String saveVariant(@ModelAttribute("variants") Variant variant) {
        Products product = variant.getProduct();
        if (product != null) {
            variantService.addVariantToTheProduct(product.getId(), variant);
            return "redirect:/variant/variantList";
        } else {
            return "404";
        }
    }

    @GetMapping("/search-variant")
    public String searchVariantByName(@RequestParam String searchTerm, Model model) {
        List<Variant> variantList = new ArrayList<>(variantRepository.findByName(searchTerm));
        model.addAttribute("variants", variantList);
        return "variant-management";
    }

    @GetMapping("/editVariant/{id}")
    public String editVariant(@PathVariable Long id, Model model) {
        Optional<Variant> optionalVariant = variantService.getVariantById(id);
        if (optionalVariant.isPresent()) {
            Variant variant = optionalVariant.get();
            model.addAttribute("product", variant.getProduct());
            model.addAttribute("variants", variant);
            return "update-variant";
        } else {
            return "404";
        }
    }

    @PostMapping("/updateVariant")
    public String updateVariant(@ModelAttribute("variants") Variant variant) {
        variantService.updateVariant(variant.getId(), variant);
        return "redirect:/variant/variantList";
    }

    @GetMapping("/get-variant/{id}")
    @ResponseBody
    public ResponseEntity<Variant> getVariantDetails(@PathVariable("id") String id) {

        try {
            Long variantId = Long.parseLong(id);
            Optional<Variant> optionalVariant = variantService.getVariantById(variantId);

            if (optionalVariant.isPresent()) {
                Variant variant = optionalVariant.get();

                return ResponseEntity.ok(variant);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.getCause();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @GetMapping("/get-variant/{id}")
//    @ResponseBody
//    public ResponseEntity<Variant> getVariantDetails(@PathVariable("id") String id) {
//        try {
//            Long variantId = Long.parseLong(id);
//            Optional<Variant> optionalVariant = variantService.getVariantById(variantId);
//
//            if (optionalVariant.isPresent()) {
//                Variant variant = optionalVariant.get();
//
//                return ResponseEntity.ok(variant);
//            } else {
//                return ResponseEntity.notFound().build();
//            }
//        } catch (NumberFormatException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
}

