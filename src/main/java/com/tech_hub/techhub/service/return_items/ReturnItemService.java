package com.tech_hub.techhub.service.return_items;

import com.tech_hub.techhub.entity.ReturnItems;

import java.util.List;
import java.util.Optional;

public interface ReturnItemService {

    void save(ReturnItems returnItems);
    List<ReturnItems>findAllReturnItems();
    Optional<ReturnItems> getReturnItemById(Long id);
}
