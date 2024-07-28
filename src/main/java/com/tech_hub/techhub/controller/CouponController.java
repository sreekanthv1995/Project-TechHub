package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.Coupon;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.service.coupon.CouponService;
import com.tech_hub.techhub.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class CouponController {


    @Autowired
    CouponService couponService;
    @Autowired
    UserService userService;

    @GetMapping("/coupons")
    public String coupons(Model model){
        model.addAttribute("coupons",couponService.getAll());
        return "coupon-management";
    }

    @GetMapping("/add/coupon")
    public String addCoupon(Model model){
        model.addAttribute("coupon",new Coupon());
        return "coupon-add";
    }
    @PostMapping("/add/save")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon){
            couponService.createCoupon(coupon);
            return "redirect:/admin/coupons";
        }


    @PostMapping("/apply-coupon")
    public String applyCoupon(@RequestParam("couponCode") String couponCode,
                              Principal principal, RedirectAttributes redirectAttributes) {

        if (principal != null) {
            Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                Cart cart = user.getCart();

                double totalPrice = cart.getCartItems().stream()
                        .mapToDouble(cartItem -> cartItem.getVariant().getPrice() * cartItem.getQuantity())
                        .sum();
                Coupon coupon = couponService.findByCouponCode(couponCode);
                if (coupon != null) {
                    if (totalPrice >= coupon.getCartAmount()) {
                        if (couponService.couponIsApplicable(coupon, totalPrice)) {
                            double discountAmount = (totalPrice * coupon.getDiscount()) / 100.0;

                            if (discountAmount > coupon.getMaxAmount()) {
                                discountAmount = coupon.getMaxAmount();
                            }
                            double totalPriceAfterDiscount = totalPrice - discountAmount;

                            redirectAttributes.addFlashAttribute("discountedTotalPrice", totalPriceAfterDiscount);
                            redirectAttributes.addFlashAttribute("couponApplied", true);
                            redirectAttributes.addFlashAttribute("couponMessage",
                                    "Coupon applied successfully!");

                            return "redirect:/cart/checkout?discountedTotalPrice=" + totalPriceAfterDiscount;
                        } else {
                            redirectAttributes.addFlashAttribute("couponError",
                                    "Invalid coupon code or coupon is not applicable.");
                            return "redirect:/cart/checkout";
                        }
                    } else {
                        redirectAttributes.addFlashAttribute("couponError",
                                "Cart total price must be at least " + coupon.getCartAmount() + " to apply a coupon.");
                        return "redirect:/cart/checkout";
                    }
                }else {
                    redirectAttributes.addFlashAttribute("couponError",
                            "Invalid coupon code.");
                    return "redirect:/cart/checkout";
                }
            }
        }
        return "redirect:/cart/checkout";
    }


}
