package com.tech_hub.techhub.service.return_items;

import com.tech_hub.techhub.entity.ReturnItems;
import com.tech_hub.techhub.repository.ReturnItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReturnServiceImpl implements ReturnItemService {

    @Autowired
    ReturnItemRepository returnItemRepository;

    @Override
    public void save(ReturnItems returnItems) {
        returnItemRepository.save(returnItems);
    }

    @Override
    public List<ReturnItems> findAllReturnItems() {
        return returnItemRepository.findAll();
    }

    @Override
    public Optional<ReturnItems> getReturnItemById(Long id) {
        return returnItemRepository.findById(id);
    }
}
