package com.tech_hub.techhub.service.variant;

import com.tech_hub.techhub.entity.CartItems;
import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Variant;

import java.util.List;
import java.util.Optional;

public interface VariantService {


    void save(Variant variant);
    List<Variant>getAll();
    Optional<Variant>getVariantById(Long id);
    void addVariantToTheProduct(Long id,Variant variant);
    void updateVariant(Long id,Variant updateVariant);
    List<Variant> getVariantByProduct(Products product);
    void reduceVariantStock(List<CartItems> cartItems);




}
