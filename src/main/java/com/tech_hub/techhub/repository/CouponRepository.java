package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon,Long> {

    Coupon findByCouponCode(String couponCode);
}
