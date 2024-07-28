package com.tech_hub.techhub.service.cart;

import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.CartItems;
import com.tech_hub.techhub.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface CartService {

    Cart createCart(UserEntity user);
    void deleteUserCart(UserEntity user);
    Optional<Cart> getCart(UserEntity user);
    Cart getCartByUserName(String username);
    void addToCart(Long id,String username);
    List<CartItems>getAllItems();
    List<CartItems>getCartItemsForUsers(String username);
    void deleteCartItemById(Long id);
    void deleteCartById(Long id);
    void deleteCart(Cart cart);
    void deleteCartItem(CartItems cartItems);
    double calculateTotalPrice(List<CartItems> cartItems);
    Optional<CartItems> getCartItemById(Long cartItemId);
    public void updateCartItem(String username,Long id,int newQuantity);
    void save(Cart cart);
    public double calculateTotalPriceWithDiscount(Double discountedTotalPrice, double totalPriceWithoutDiscount);
}
