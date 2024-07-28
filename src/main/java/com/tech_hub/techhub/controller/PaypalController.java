package com.tech_hub.techhub.controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.tech_hub.techhub.entity.PaymentMode;
import com.tech_hub.techhub.service.paypal.PaypalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class PaypalController {


    @Autowired
    PaypalService paypalService;

    public static final String SUCCESS_URL = "success";
    public static final String CANCEL_URL = "cancel";
    public String currency ="USD";
    public static String intent = "sale";
    public static String description = "testing";

    @GetMapping("/pay")
    public String payment (@RequestParam("totalPrice")double totalPrice,
                           @RequestParam("paymentMode")PaymentMode paymentMode){
        try{
            Payment payment = paypalService.createPayment(totalPrice,currency,paymentMode.toString(),intent,description,
                    "http://localhost:8080/"+CANCEL_URL,"http://localhost:8080/"+SUCCESS_URL);


                for (Links link: payment.getLinks()){
                    if (link.getRel().equals("approval_url")){
                        return "redirect:"+link.getHref();
                    }
                }
        }catch (PayPalRESTException e){

            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping(value = CANCEL_URL)
    public String cancelPay(){
        return "cancel";
    }
    @GetMapping(value = SUCCESS_URL)
    public String successPay(@RequestParam("paymentId") String paymentId,@RequestParam("payerId")String payerId){
        try {
            Payment payment = paypalService.executePaymentMethod(paymentId,payerId);
            if (payment.getState().equals("approved")){
                return "success";
            }
        }catch (PayPalRESTException e){
            log.error(e.getMessage());

        }
        return "redirect:/";

    }
}
