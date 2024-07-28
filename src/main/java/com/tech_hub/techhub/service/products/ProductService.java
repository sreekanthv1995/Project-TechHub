package com.tech_hub.techhub.service.products;




import com.tech_hub.techhub.dto.ProductDto;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Variant;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {

     List<Products> getAllProducts();
    public void addProduct(ProductDto productDto, List<MultipartFile> files) throws IOException;
     void removeProductById(Long id);
     Optional<Products> getProductById(Long id);
     List<Products> getAllProductsByCategoryId(Long id);
    void updateProduct(Long id, ProductDto productDto);
    Page<Products> findAllByPage(int pageNo,int pageSize,String field,String direction);
    Page<Products> findProductsByCategoryIdAndPage(Long id, int pageNo, int pageSize, String field, String sortDirection);

    List<Variant> getVariantForProduct(Products product);

    Products getProductByVariantId(Long id);
}

