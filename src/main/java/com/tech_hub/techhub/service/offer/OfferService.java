package com.tech_hub.techhub.service.offer;

import com.tech_hub.techhub.entity.Offer;

import java.util.List;
import java.util.Optional;

public interface OfferService {

    void save(Offer offer);
    List<Offer> getAllOffers();
    void createOffer(Long id,int offer);
    Optional<Offer> findById(Integer id);
    void disableOffer(Integer id);
    void enableOffer(Integer id);
}
