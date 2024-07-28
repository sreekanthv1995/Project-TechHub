package com.tech_hub.techhub.service.coupon;

import com.tech_hub.techhub.entity.Coupon;

import java.security.Principal;
import java.util.List;

public interface CouponService {


    List<Coupon> getAll();
    void deleteCategory(Long id);
    void createCoupon(Coupon coupon);

    double applyCoupon(Principal principal, Coupon coupon);

    Coupon findByCouponCode(String couponCode);
    boolean couponIsApplicable(Coupon coupon,double totalPrice);

//    void decreaseStock(Coupon coupon);
}
