package com.tech_hub.techhub.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private Long categoryId;
    private Long variantId;
    private double price;
    private String description;
    private Integer stock;
    private LocalDate createdAt;
    private List<MultipartFile> productImages = new ArrayList<>();
}
