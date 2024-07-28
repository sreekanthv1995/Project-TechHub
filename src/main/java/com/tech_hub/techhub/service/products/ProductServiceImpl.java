package com.tech_hub.techhub.service.products;


import com.tech_hub.techhub.dto.ProductDto;
import com.tech_hub.techhub.entity.CartItems;
import com.tech_hub.techhub.entity.Image;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Category;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.exception.ProductInCartException;
import com.tech_hub.techhub.repository.CartItemRepository;
import com.tech_hub.techhub.repository.ProductRepository;
import com.tech_hub.techhub.repository.VariantRepository;
import com.tech_hub.techhub.service.category.CategoryService;
import com.tech_hub.techhub.service.cloudinary.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductRepository productRepository;
    @Autowired
    VariantRepository variantRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    CategoryService categoryService;
    @Autowired
    CloudinaryService cloudinaryService;


    static final String UPLOAD_DIR = "/Users/sreekanth/Desktop/E-commerce Project/src/main/resources/static/productImages";

    @Override
    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }
//    @Override
//    public void addProduct(ProductDto productDto,List<MultipartFile> files) throws IOException {
//
//        Products product = new Products();
//        Optional<Category> optionalCategory = categoryService.getCategoryById(productDto.getCategoryId());
//        if (optionalCategory.isPresent()) {
//            Category category = optionalCategory.get();
//
//            product.setId(productDto.getId());
//            product.setName(productDto.getName());
//            product.setCategory(category);
//            product.setDescription(productDto.getDescription());
//            product.setCreatedAt(LocalDate.now());
//            List<Image> images = new ArrayList<>();
//
//            for (MultipartFile file : files) {
//                Image image = new Image();
//                String imageUUID = file.getOriginalFilename();
//                Path filenameAndPath = Paths.get(UPLOAD_DIR, imageUUID);
//                Files.write(filenameAndPath, file.getBytes());
//                image.setFileName(imageUUID);
//                image.setProducts(product);
//                images.add(image);
//            }
//            product.setImages(images);
//            productRepository.save(product);
//        }
//    }

    @Override
    public void addProduct(ProductDto productDto,List<MultipartFile> files) throws IOException {

        Products product = new Products();
        Optional<Category> optionalCategory = categoryService.getCategoryById(productDto.getCategoryId());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();

            product.setId(productDto.getId());
            product.setName(productDto.getName());
            product.setCategory(category);
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(LocalDate.now());
            List<Image> images = new ArrayList<>();

            for (MultipartFile file : files) {
                Image image = new Image();
                Map<?,?> uploadFile = cloudinaryService.uploadImage(file,"E_Com_Products");
                String imageUrl = (String) uploadFile.get("url");
                String fileName = (String) uploadFile.get("public_id");
                image.setFileName(fileName);
                image.setImageUrl(imageUrl);
                image.setProducts(product);
                images.add(image);
            }
            product.setImages(images);
            productRepository.save(product);
        }
    }
    @Override
    public void removeProductById(Long id) {
        try {
            Products product = productRepository.findById(id).orElseThrow
                    (() -> new EntityNotFoundException("Product Not Found"));
            List<Variant>variants =product.getVariant();
            for (Variant variant : variants){
                CartItems cartItem = cartItemRepository.findByVariant(variant);
                if (cartItem != null){
                    throw new ProductInCartException("Can't delete this product; it's present in a user's cart.");
                }else {
                    variantRepository.delete(variant);
                }
            }
            productRepository.delete(product);
        }catch (ProductInCartException e){
            e.printStackTrace();
        }
    }

    public Optional<Products> getProductById(Long id){
        return productRepository.findById(id);
    }
    @Override
    public List<Products> getAllProductsByCategoryId(Long id) {
        return productRepository.findAllByCategoryId(id);
    }


    @Override
    public void updateProduct(Long id, ProductDto productDto) {
        Optional<Products> optionalProducts = productRepository.findById(id);
        if (optionalProducts.isPresent()){
            Products product = optionalProducts.get();
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            productRepository.save(product);
        }
    }

    @Override
    public Page<Products> findAllByPage(int pageNo, int pageSize,String field,String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize,sort);
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Products> findProductsByCategoryIdAndPage(Long id, int pageNo, int pageSize, String field, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNo-1, pageSize,sort);
        return productRepository.findAllByCategoryId(pageable,id);
    }

    @Override
    public List<Variant> getVariantForProduct(Products product) {
       return variantRepository.findByProduct(product);
    }

    @Override
    public Products getProductByVariantId(Long id) {
        return productRepository.findProductByVariantId(id);
    }



}
