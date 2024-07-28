package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.entity.Status;
import com.tech_hub.techhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Cart> getOrderByUser(UserEntity user);
    Optional<Order> findFirstByUserOrderByOrderDateAsc(UserEntity user);

    List<Order> findByUser(UserEntity user);
    List<Order> findByOrderDate(LocalDate date);
    List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate);
    Long countByStatus(Status status);

}