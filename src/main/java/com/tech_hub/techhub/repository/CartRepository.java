package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUser(UserEntity user);
    Cart findByUserUsername(String username);

}
