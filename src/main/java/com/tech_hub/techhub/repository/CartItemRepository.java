package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.CartItems;
import com.tech_hub.techhub.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems,Long> {

    CartItems findByVariant(Variant variant);





}
