package com.tech_hub.techhub.controller;

import com.tech_hub.techhub.entity.Offer;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.service.offer.OfferService;
import com.tech_hub.techhub.service.variant.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class OfferController {

    @Autowired
    OfferService offerService;
    @Autowired
    VariantService variantService;
    private static final String OFFER_LIST_REDIRECT = "redirect:/admin/offer/list";

    @GetMapping("/offer/list")
    public String showOffers(Model model){
        List<Variant> variants = variantService.getAll();
        model.addAttribute("variants",variants);
        model.addAttribute("offers",offerService.getAllOffers());
        return "offer-management";
    }

    @GetMapping("/add/offer")
    public String addOffer(Model model){
        List<Variant> variants = variantService.getAll();
        model.addAttribute("variants",variants);
        model.addAttribute("offers",new Offer());
        return "create-offer";
    }
@PostMapping("/save/offer")
public String saveOffer(@RequestParam("discount")int discount,
                        @RequestParam("variant_id")Long id){
    offerService.createOffer(id,discount);
    return OFFER_LIST_REDIRECT;
}

    @GetMapping("/disable/offer/{id}")
    public String disableOffer(@PathVariable("id")Integer id){
        offerService.disableOffer(id);
            return OFFER_LIST_REDIRECT;
    }
    @GetMapping("/enable/offer/{id}")
    public String enableOffer(@PathVariable("id")Integer id){
        offerService.enableOffer(id);
        return OFFER_LIST_REDIRECT;
    }


}
