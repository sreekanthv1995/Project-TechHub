package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Products;
import com.tech_hub.techhub.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant,Long> {

    @Query("SELECT v FROM Variant v WHERE v.variantName LIKE %?1%")
    List<Variant> findByName(String variantName);
    List<Variant> findByProduct(Products products);

}
