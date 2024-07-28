package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.service.cancelled_items.CancelledItemService;
import com.tech_hub.techhub.service.orders.OrderService;
import com.tech_hub.techhub.service.return_items.ReturnItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    CancelledItemService cancelledItemService;
    @Autowired
    ReturnItemService returnItemService;

    @GetMapping("/view-orders")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String viewAllOrders(Model model){
        List<Order> allOrders = orderService.findAll();
        List<Order> orders = allOrders.stream().sorted(Comparator
                .comparing(Order::getOrderDate).reversed()).collect(Collectors.toList());
        Collections.reverse(orders);
        model.addAttribute("orders",orders);
        return"order-management";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String viewOrder(@PathVariable("id")Long id,Model model){

        Optional<Order> orders = orderService.findById(id);
        if (orders.isPresent()) {
            Order order = orders.get();
            model.addAttribute("order", order);
            return "single-order";
        }
        return "404";
    }

    @PostMapping("/updateOrderStatus")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String updateOrderStatus(@ModelAttribute("order")Order order){
        orderService.updateOrderStatus(order.getId(),order.getStatus());
        return "redirect:/admin/view/"+order.getId();

    }

    @GetMapping("/cancelled-items")
    public String showCancelledList(Model model){
      model.addAttribute("cancelledItems",cancelledItemService.findAllCancelledItems());
        return "cancelled-items";

    }
    @GetMapping("/return-items")
    public String showReturnList(Model model){
        model.addAttribute("returnItems",returnItemService.findAllReturnItems());
        return "return-items";
    }

    @GetMapping("/proceed/refund/{id}")
    public String proceedRefund(@PathVariable("id")Long id){
        orderService.proceedRefund(id);
        return "redirect:/admin/return-items";

    }










}
