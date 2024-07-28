package com.tech_hub.techhub.service.coupon;

import com.tech_hub.techhub.entity.*;
import com.tech_hub.techhub.repository.CouponRepository;
import com.tech_hub.techhub.service.products.ProductService;
import com.tech_hub.techhub.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CouponServiceImpl implements CouponService{

    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;

    @Override
    public List<Coupon> getAll() {
        return couponRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        couponRepository.deleteById(id);

    }

    @Override
    public void createCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    @Override
    public double applyCoupon(Principal principal, Coupon coupon) {

        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();

            Cart cart = user.getCart();
            List<CartItems> cartItem = cart.getCartItems();
            double totalPrice = cartItem.stream().mapToDouble(cartItems ->
                    cartItems.getVariant().getPrice() * cartItems.getQuantity()).sum();

            if (totalPrice >= coupon.getCartAmount() && couponIsApplicable(coupon,totalPrice)){
                double discountPrice = (totalPrice * coupon.getDiscount())/100.0;

                return totalPrice - discountPrice;
            }
        }
        return -1.0;

    }

    @Override
    public Coupon findByCouponCode(String couponCode) {
       return couponRepository.findByCouponCode(couponCode);
    }

    @Override
     public boolean couponIsApplicable(Coupon coupon, double totalPrice){
        LocalDate currentDate = LocalDate.now();
        return coupon!=null &&
                !currentDate.isAfter(coupon.getExpiryDate()) &&
                coupon.getCouponStock() > 0 && totalPrice >=coupon.getMaxAmount();
    }

//    @Override
//    public void decreaseStock(Coupon coupon) {
//        Optional<Coupon> optionalCoupon = couponRepository.findById(coupon.getId());
//        if (optionalCoupon.isPresent()){
//            Coupon coupon1 = optionalCoupon.get();
//            coupon1.setCouponStock(coupon1.getCouponStock()-1);
//        }
//
//    }
}
