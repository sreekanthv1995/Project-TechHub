package com.tech_hub.techhub.service.cancelled_items;

import com.tech_hub.techhub.entity.CancelledItems;
import com.tech_hub.techhub.repository.CancelledItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CancelledItemServiceImpl implements CancelledItemService{

    @Autowired
    CancelledItemsRepository cancelledItemsRepository;

    @Override
    public void save(CancelledItems cancelledItems) {
        cancelledItemsRepository.save(cancelledItems);
    }

    @Override
    public List<CancelledItems> findAllCancelledItems() {
        return cancelledItemsRepository.findAll();
    }
}
