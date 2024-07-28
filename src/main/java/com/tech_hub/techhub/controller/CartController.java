package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.configuration.CustomUserDetails;
import com.tech_hub.techhub.entity.*;
import com.tech_hub.techhub.service.address.AddressService;
import com.tech_hub.techhub.service.cart.CartService;
import com.tech_hub.techhub.service.coupon.CouponService;
import com.tech_hub.techhub.service.orders.OrderService;
import com.tech_hub.techhub.service.user.UserService;
import com.tech_hub.techhub.service.variant.VariantService;
import com.tech_hub.techhub.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;
    @Autowired
    VariantService variantService;
    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @Autowired
    OrderService orderService;
    @Autowired
    CouponService couponService;
    @Autowired
    WalletService walletService;

    private static final String ORDER_ATTRIBUTE = "order";
    private static final String EXPECT_DELIVERY_DATE = "expectedDeliveryDate";
    private static final String ORDER_CONFIRMATION = "order-confirmation";
    private static final String CHECKOUT_REDIRECT = "redirect:/cart/checkout";
    private static final String LOGIN_REDIRECT = "redirect:/login";


    @GetMapping("/add/{id}")
    @ResponseBody
    public ResponseEntity<String> addCart(@PathVariable("id") Long id, Authentication authentication) {
        try {
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal()
                    instanceof CustomUserDetails customUserDetails) {
                Variant variant = variantService.getVariantById(id).orElseThrow();

                if (variant.getStock() <= 0) {
                    return ResponseEntity.ok("Out of Stock");
                }
                cartService.addToCart(id, customUserDetails.getUsername());
                return ResponseEntity.ok("Added to cart");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/cart-items")
    public String showCart(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetails customUserDetails) {
            List<CartItems> cartItems = cartService.getCartItemsForUsers(customUserDetails.getUsername());
            model.addAttribute("cartItems", cartItems);
            return "cart";
        } else {
            return LOGIN_REDIRECT;
        }
    }

    @GetMapping("delete/{id}")
    public String deleteCartItem(@PathVariable Long id) {
        cartService.deleteCartItemById(id);
        return "redirect:/cart/cart-items";
    }

    @PostMapping("/update-cart")
    public String updateCart(@RequestParam Long id,
                             @RequestParam int newQuantity,
                             Principal principal) {
        if (principal == null) {
            return LOGIN_REDIRECT;
        } else {
            String username = principal.getName();
            Optional<Variant> optionalVariant = variantService.getVariantById(id);
            if (optionalVariant.isPresent()) {
                Variant variant = optionalVariant.get();
                cartService.updateCartItem(username, variant.getId(), newQuantity);
                return "redirect:/cart/cart-items";
            }
        }
        return "404";
    }

    @GetMapping("/checkout")
    public String showCheckout(Model model, Principal principal,
                               @RequestParam(value = "discountedTotalPrice",
                                       required = false) Double discountedTotalPrice) {

        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()) {
            UserEntity user = optionalUser.get();
            List<Address> addressList = addressService.findAllUserAddresses(user).stream()
                    .filter(address -> !address.isDelete()).sorted(Comparator.comparing(Address::getCreatedAt).reversed()).toList();
            model.addAttribute("addresses", addressList);
            model.addAttribute("user", user);

            Cart cart = user.getCart();
            List<CartItems> cartItems = cart.getCartItems();
            model.addAttribute("cartItems", cartItems);

            List<Coupon> coupons = couponService.getAll();

            double totalPrice = cartItems.stream()
                    .mapToDouble(cartItem -> cartItem.getVariant().getPrice() * cartItem.getQuantity())
                    .sum();
            model.addAttribute("totalPrice", (discountedTotalPrice != null) ? discountedTotalPrice : totalPrice);
            model.addAttribute("coupons", coupons);
        }
        return "checkout";
    }


    @PostMapping("/place-order")
    public String placeOrder(Model model,
                             @RequestParam(value = "selectedAddressId", required = false) Integer addressId,
                             @RequestParam(value = "paymentMethod", required = false) PaymentMode selectedPaymentMode,
                             @RequestParam(value = "discountedTotalPrice", required = false) Double discountedTotalPrice,
                             Principal principal, RedirectAttributes redirectAttributes) {

        if (addressId == null) {
            model.addAttribute("error", "Please select an address.");
            return CHECKOUT_REDIRECT;
        }

        if (principal != null) {
            Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();

                Cart userCart = user.getCart();
                List<CartItems> cartItem = userCart.getCartItems();

                double totalPriceWithoutDiscount = cartService.calculateTotalPrice(cartItem);
                double totalPrice = cartService.calculateTotalPriceWithDiscount(discountedTotalPrice, totalPriceWithoutDiscount);

                Optional<Address> optionalUserAddress = addressService.findById(addressId);
                if (optionalUserAddress.isPresent()) {
                    Address userAddress = optionalUserAddress.get();
                    try {
                        Order order = orderService.saveOrder(user, cartItem, totalPrice, selectedPaymentMode, userAddress);
                        LocalDate expectedDeliveryDate = order.getOrderDate().plusDays(7);

                        if (isPaypal(selectedPaymentMode)) {
                            return "redirect:/pay?totalPrice=" + totalPrice + "&paymentMode=PAYPAL";
                        } else if (isRazorpay(selectedPaymentMode)) {
                            return "redirect:/payment/" + totalPrice + "?addressId=" + addressId + "&orderId=" + order.getId();
                        } else if (isWallet(selectedPaymentMode)) {
                            Optional<Wallet> optionalWallet = walletService.getWallet(user);
                            if (optionalWallet.isPresent()) {
                                Wallet userWallet = optionalWallet.get();
                                if (totalPrice <= userWallet.getWalletAmount()) {
                                    handleWalletPayment(model, userCart, cartItem, totalPrice, order, expectedDeliveryDate, userWallet);
                                    return ORDER_CONFIRMATION;
                                } else {
                                    redirectAttributes.addFlashAttribute("error", "insufficient fund");
                                    return CHECKOUT_REDIRECT;
                                }
                            }
                        } else if (isCod(selectedPaymentMode)) {
                            handleCodPayment(model, userCart, cartItem, order, expectedDeliveryDate);
                            return ORDER_CONFIRMATION;
                        } else {
                            model.addAttribute("message", "payment method not selected");
                            return CHECKOUT_REDIRECT;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                model.addAttribute("message", "please select address");
                return CHECKOUT_REDIRECT;
            }
        }
        return LOGIN_REDIRECT;
    }

    private static boolean isRazorpay(PaymentMode selectedPaymentMode) {
        return selectedPaymentMode == PaymentMode.RAZORPAY;
    }

    private static boolean isCod(PaymentMode selectedPaymentMode) {
        return selectedPaymentMode == PaymentMode.COD;
    }

    private static boolean isWallet(PaymentMode selectedPaymentMode) {
        return selectedPaymentMode == PaymentMode.WALLET;
    }

    private static boolean isPaypal(PaymentMode selectedPaymentMode) {
        return selectedPaymentMode == PaymentMode.PAYPAL;
    }

    private void handleWalletPayment(Model model, Cart userCart, List<CartItems> cartItem, double totalPrice, Order order, LocalDate expectedDeliveryDate, Wallet userWallet) {
        userWallet.setWalletAmount(userWallet.getWalletAmount() - totalPrice);
        walletService.save(userWallet);
        variantService.reduceVariantStock(cartItem);
        userService.deleteCart(userCart);
        cartService.deleteCart(userCart);
        model.addAttribute(ORDER_ATTRIBUTE, order);
        model.addAttribute(EXPECT_DELIVERY_DATE, expectedDeliveryDate);

    }

    private void handleCodPayment(Model model, Cart userCart, List<CartItems> cartItem, Order order, LocalDate expectedDeliveryDate) {
        variantService.reduceVariantStock(cartItem);
        userService.deleteCart(userCart);
        cartService.deleteCart(userCart);
        model.addAttribute(ORDER_ATTRIBUTE, order);
        model.addAttribute(EXPECT_DELIVERY_DATE, expectedDeliveryDate);

    }


    @GetMapping("/order-confirmation")
    public String confirmOrder(@RequestParam(name = "id", required = false) String paymentId,
                               @RequestParam(name = "orderId", required = false) String orderId,
                               Model model, Principal principal) {
        try {
            Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();
                Cart userCart = user.getCart();
                List<CartItems> cartItem = userCart.getCartItems();
                Optional<Order> optionalUserOrder = orderService.findById(Long.valueOf(orderId));
                if (optionalUserOrder.isPresent()) {
                    Order userOrder = optionalUserOrder.get();
                    Address userAddress = userOrder.getAddress();
                    variantService.reduceVariantStock(cartItem);

                    LocalDate expectedDeliveryDate = userOrder.getOrderDate().plusDays(7);

                    userOrder.setStatus(Status.CONFIRMED);
                    orderService.save(userOrder);
                    userService.deleteCart(userCart);
                    cartService.deleteCart(userCart);

                    model.addAttribute(EXPECT_DELIVERY_DATE, expectedDeliveryDate);
                    model.addAttribute("paymentId", paymentId);
                    model.addAttribute("address", userAddress);
                    model.addAttribute(ORDER_ATTRIBUTE, userOrder);
                    return ORDER_CONFIRMATION;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "404";
    }


}

