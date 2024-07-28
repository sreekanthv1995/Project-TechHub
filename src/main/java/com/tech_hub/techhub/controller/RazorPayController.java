package com.tech_hub.techhub.controller;


import com.tech_hub.techhub.entity.Address;
import com.tech_hub.techhub.entity.Order;
import com.tech_hub.techhub.entity.TransactionDetails;
import com.tech_hub.techhub.service.address.AddressService;
import com.tech_hub.techhub.service.orders.OrderService;
import com.tech_hub.techhub.service.razorpay.RazorPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;


@Controller
public class RazorPayController {

    @Autowired
    RazorPayService razorPayService;
    @Autowired
    AddressService addressService;
    @Autowired
    OrderService orderService;

    @GetMapping("/payment/{amount}")
    public String createTransaction(@PathVariable("amount") Double amount,
                                    @RequestParam("addressId") Integer id,
                                    @RequestParam("orderId") Long orderId,
                                    Model model) {

        Optional<Order> optionalOrder = orderService.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            Optional<Address> optionalAddress = addressService.findById(id);
            if (optionalAddress.isPresent()) {
                Address userAddress = optionalAddress.get();

                TransactionDetails transactionDetails = razorPayService.createTransaction(amount);

                if (transactionDetails != null) {
                    model.addAttribute("order", order);
                    model.addAttribute("transactionDetails", transactionDetails);
                    model.addAttribute("address", userAddress);

                    return "razorpay";
                } else {
                    return "404";
                }
            }
        }
        return "404";
    }

}
