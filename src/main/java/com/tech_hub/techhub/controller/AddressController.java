package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.dto.AddressDto;
import com.tech_hub.techhub.entity.Address;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.exception.AddressNotFoundException;
import com.tech_hub.techhub.service.address.AddressService;
import com.tech_hub.techhub.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class AddressController {

    @Autowired
    UserService userService;
    @Autowired
    AddressService addressService;

    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/edit-address/{id}")
    public String editAddress(@PathVariable("id") Integer id,
                              Model model, Principal principal,
                              @RequestParam(name = "editSource", required = false, defaultValue = "profile") String editSource) {
        UserEntity user = userService.findByUsername(principal.getName()).orElse(null);

        if (user != null) {
            Optional<Address> optionalAddress = addressService.findById(id);
            if (optionalAddress.isPresent()) {
                Address userAddress = optionalAddress.get();
                model.addAttribute("address", userAddress);
                model.addAttribute("userObj", user);
                model.addAttribute("editSource", editSource);
                return "user/edit-address";
            }
        }
        return "404";
    }

    @PostMapping("/update-address")
    public String updateAddress(@ModelAttribute("address") AddressDto addressDto,
                                @RequestParam("editSource") String editSource) throws AddressNotFoundException {
        addressService.editAddress(addressDto.getId(),addressDto);
        if ("profile".equals(editSource)) {
            return "redirect:/user/address";
        } else if ("checkout".equals(editSource)) {
            return "redirect:/cart/checkout";
        } else {
            return "404";
        }
    }
    @GetMapping("/address")
    public String userAddressList(Model model){
        UserEntity user = userService.findByUsername(getCurrentUserName()).orElse(null);
        assert user != null;
        List<Address> userAddress =addressService.findAllUserAddresses(user).stream().
                filter(address -> !address.isDelete()).toList();
        model.addAttribute("user",user);
        model.addAttribute("userAddress",userAddress);
        return "user/user-address";
    }
    @GetMapping("/add-address")
    public String addAddressForm(Model model,Principal principal,
                                 @RequestParam(name = "editSource", required = false, defaultValue = "profile") String editSource){
        model.addAttribute("username",principal.getName());
        model.addAttribute("addressDto",new AddressDto());
        model.addAttribute("editSource", editSource);
        return "address-form";
    }
    @PostMapping("/save-address")
    public String addAddress(@ModelAttribute("add-address")AddressDto addressDto,
                             @RequestParam("editSource") String editSource,
                             Model model){
        UserEntity user = userService.findByUsername(getCurrentUserName()).orElse(null);
        addressService.addNewAddress(addressDto,user);
        if ("profile".equals(editSource)){
            model.addAttribute("user",user);
            return "redirect:/user/address";
        } else if ("checkout".equals(editSource)) {
            model.addAttribute("user",user);
            return "redirect:/cart/checkout";
        }else {
            return "404";
        }
    }
    @GetMapping("/delete/address/{id}")
    public String deleteAddress(@PathVariable("id") Integer id){
        Optional<Address> optionalAddress = addressService.findById(id);
        if (optionalAddress.isPresent()){
            Address address = optionalAddress.get();
            address.setDelete(true);
            addressService.saveAddress(address);
        }
        return "redirect:/user/address";
    }
}
