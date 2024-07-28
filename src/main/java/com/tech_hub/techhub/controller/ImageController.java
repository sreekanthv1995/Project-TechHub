package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.service.image.ImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageServiceImpl imageService;

    @GetMapping("/deleteImage/{id}")
    public String deleteImage(@PathVariable("id") Long id){
        imageService.deleteImageById(id);
        return "redirect:/admin/products";

    }
}
