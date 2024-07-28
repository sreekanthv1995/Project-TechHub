package com.tech_hub.techhub.service.offer;

import com.tech_hub.techhub.entity.Offer;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.entity.Variant;
import com.tech_hub.techhub.repository.OfferRepository;
import com.tech_hub.techhub.service.variant.VariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService {
    @Autowired
    VariantService variantService;

    @Autowired
    OfferRepository offerRepository;

    @Override
    public void save(Offer offer) {
        offerRepository.save(offer);
    }

    @Override
    public List<Offer> getAllOffers() {
        return offerRepository.findAll();
    }

    @Override
    public void createOffer(Long id, int discount) {
        Optional<Variant> optionalVariant = variantService.getVariantById(id);
        if (optionalVariant.isPresent()) {
            Variant variant = optionalVariant.get();
            if (variant.getDiscount() == null) {
                Offer offer = new Offer();
                offer.setDiscount(discount);
                variant.setDiscount(offer);
                offer.setVariant(variant);
            } else {
                variant.getDiscount().setDiscount(discount);
            }
            variantService.save(variant);
        }
    }

    @Override
    public Optional<Offer> findById(Integer id) {
        return offerRepository.findById(id);
    }
    @Override
    public void disableOffer(Integer id) {
        Offer offer = offerRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("offer Not Found With Id"+id));
            offer.setEnabled(true);
            Variant variant = offer.getVariant();
            if (variant != null) {
                float discount = (float) variant.getDiscount().getDiscount() / 100 * variant.getPrice();
                float offerPrice = variant.getPrice() - discount;
                variant.setPrice(offerPrice);
                variantService.save(variant);
                offerRepository.save(offer);
            }
    }

    @Override
    public void enableOffer(Integer id) {
        Offer offer = offerRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("offer Not Found With Id"+id));
        offer.setEnabled(false);
        Variant variant = offer.getVariant();
        if (variant != null) {
            variant.setPrice(variant.getOriginalPrice());
            variantService.save(variant);
            offerRepository.save(offer);
        }
    }
}
