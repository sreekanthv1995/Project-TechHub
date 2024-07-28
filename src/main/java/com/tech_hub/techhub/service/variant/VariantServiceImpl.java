package com.tech_hub.techhub.service.variant;

import com.tech_hub.techhub.entity.CartItems;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.repository.VariantRepository;
import com.tech_hub.techhub.service.products.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VariantServiceImpl implements VariantService{

    @Autowired
    VariantRepository variantRepository;
    @Autowired
    ProductServiceImpl productService;


    @Override
    public void save(Variant variant) {
        variantRepository.save(variant);
    }

    @Override
    public List<Variant> getAll() {
        return variantRepository.findAll();
    }

    @Override
    public Optional<Variant> getVariantById(Long id) {
        return variantRepository.findById(id);
    }

    @Override
    public void addVariantToTheProduct(Long id, Variant variant) {
        Optional<Products> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()){
            Products product = productOptional.get();
            variant.setProduct(product);
            variant.setOriginalPrice(variant.getPrice());
            variantRepository.save(variant);
        }
    }

    @Override
    public void updateVariant(Long id,Variant updateVariant) {
        Optional<Variant> optionalVariant = variantRepository.findById(id);
        if (optionalVariant.isPresent()){

            Variant getVariant = optionalVariant.get();
            getVariant.setProduct(updateVariant.getProduct());
            getVariant.setVariantName(updateVariant.getVariantName());
            getVariant.setPrice(updateVariant.getPrice());
            getVariant.setStock(updateVariant.getStock());

            variantRepository.save(getVariant);
        }
    }

    @Override
    public List<Variant> getVariantByProduct(Products product) {
        return variantRepository.findByProduct(product);
    }

    @Override
    public void reduceVariantStock(List<CartItems> cartItems) {

        for (CartItems cartItem : cartItems) {
            Variant variant = cartItem.getVariant();
            int orderQuantity = cartItem.getQuantity();
            int currentQuantity = variant.getStock();

            if (currentQuantity >= orderQuantity) {
                variant.setStock(currentQuantity - orderQuantity);
            } else {
                throw new RuntimeException("out of stock");
            }
            variantRepository.save(variant);
        }
    }
}
