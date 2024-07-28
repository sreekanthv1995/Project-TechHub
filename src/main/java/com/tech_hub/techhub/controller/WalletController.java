package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.TransactionDetails;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.entity.Wallet;
import com.tech_hub.techhub.service.razorpay.RazorPayService;
import com.tech_hub.techhub.service.user.UserService;
import com.tech_hub.techhub.service.wallet.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class WalletController {

    @Autowired
    WalletService walletService;
    @Autowired
    UserService userService;
    @Autowired
    RazorPayService razorPayService;



    @GetMapping("/wallet")
    public String showUserWallet(Model model, Principal principal){

        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            Optional<Wallet> optionalUserWallet = walletService.getWallet(user);
            if (optionalUserWallet.isPresent()){
                Wallet userWallet = optionalUserWallet.get();
                model.addAttribute("user",user);
                model.addAttribute("userWallet",userWallet);
                return "user/user-wallet";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/add/money")
    public String addMoneyToWallet(@RequestParam("walletAmount")Double amount,Model model,Principal principal){
        Optional<UserEntity> optionalUser = userService.findByUsername(principal.getName());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            Wallet wallet = user.getWallet();
            wallet.setWalletAmount(wallet.getWalletAmount()+amount);
            walletService.save(wallet);
        }
        TransactionDetails transactionDetails = razorPayService.createTransaction(amount);
        model.addAttribute("transactionDetails",transactionDetails);
        return "add-to-wallet";
    }


    @GetMapping("/add/success")
    public String addMoneySuccess(){
        return "redirect:/user/wallet";
    }



}

