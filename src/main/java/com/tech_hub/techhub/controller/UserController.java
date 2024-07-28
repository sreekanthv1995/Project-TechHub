package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.*;
import com.tech_hub.techhub.service.address.AddressService;
import com.tech_hub.techhub.service.orders.OrderService;
import com.tech_hub.techhub.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;
    @Autowired
    OrderService orderService;
    


    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/user-home")
    public String showUserProfile(Model model){
        UserEntity user = userService.findByUsername(getCurrentUserName()).orElse(null);
        model.addAttribute("user",user);
        return "user/user-dashboard";
    }

    @GetMapping("/my-orders")
    public String showAllOrders(Model model, Principal principal){

        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            List<Order> orders = orderService.findByUser(user);
            List<Order> userOrders = orders.stream()
                    .sorted(Comparator.comparing(Order::getOrderDate).reversed())
                    .collect(Collectors.toList());

            model.addAttribute("user",user);
            model.addAttribute("userOrders",userOrders);
        }
        return "user/order-list";
    }

    @GetMapping("/edit-user-details/{id}")
    public String editUser(@PathVariable("id")Long id,Model model){
        Optional<UserEntity> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            model.addAttribute("user",user);
            return "user/edit-profile";
        }else {
            return "404";
        }
    }
    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") UserEntity updatedUser) {
        userService.updateUser(updatedUser.getId(),updatedUser);
        return "redirect:/user/user-home";
    }


    @GetMapping("/view-order/{id}")
    public String viewOrder(@PathVariable("id")Long id,Model model,Principal principal){

        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
        Optional<Order> optionalOrder = orderService.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            model.addAttribute("order", order);
            model.addAttribute("user", user);
            return "user/view-order";
            }
        }else {
            return "redirect:/login";
        }
        return "404";
    }

    @GetMapping("/getOrderStatus/{id}")
    @ResponseBody
    public ResponseEntity<String> getOrderStatus(@PathVariable Long id){
        String orderStatus = String.valueOf(orderService.getOrderStatusById(id));
        if (orderStatus != null){
            return ResponseEntity.ok(orderStatus);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/cancel-order")
    public String cancelOrder(@RequestParam("orderId") Long orderId,
                              @RequestParam("selectedReason") CancelReasons reasons){
        orderService.cancelOrder(orderId,reasons);
        return "redirect:/user/view-order/" + orderId;
    }

    @PostMapping("/return-order")
    public String returnOrder(@RequestParam("orderId") Long orderId,
                              @RequestParam("selectedReason") CancelReasons reasons){
        orderService.returnOrder(orderId,reasons);
        return "redirect:/user/view-order/" + orderId;
    }
}










