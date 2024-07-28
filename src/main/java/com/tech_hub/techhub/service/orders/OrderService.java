package com.tech_hub.techhub.service.orders;

import com.tech_hub.techhub.entity.*;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order saveOrder(UserEntity user, List<CartItems> cartItems, Double totalPrice,
                    PaymentMode selectedPaymentMode, Address userAddress);
    Optional<Cart> getOrderByUser(UserEntity user);
    Optional<Order> findById(Long orderId);
    void save(Order order);
    Optional<Order> findFirstByUserOrderByOrderDateAsc(UserEntity user);
    List<Order> findAll();
    List<Order> findByUser(UserEntity user);
    String generateOrderId();
    Integer calculateItemCount(Order order);
    void updateOrderStatus(Long id, Status status);
    Status getOrderStatusById(Long id);
    void cancelOrder(Long id,CancelReasons reasons);
    public void returnOrder(Long id, CancelReasons reason);
    void proceedRefund(Long id);

}
