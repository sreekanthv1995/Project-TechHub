package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findAllByCategoryId(Long id);

    @Query("SELECT p FROM Products p WHERE p.name LIKE %?1% OR p.description LIKE %?1%")
    List<Products> findByName(String name);

    Page<Products> findAllByCategoryId(Pageable pageable, Long id);
    Products findProductByVariantId(Long id);
}

