package com.tech_hub.techhub.service.cancelled_items;

import com.tech_hub.techhub.entity.CancelledItems;

import java.util.List;

public interface CancelledItemService {

    void save(CancelledItems cancelledItems);
    List<CancelledItems> findAllCancelledItems();
}
