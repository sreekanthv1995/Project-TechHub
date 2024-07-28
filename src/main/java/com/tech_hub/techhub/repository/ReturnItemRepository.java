package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.ReturnItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnItemRepository extends JpaRepository<ReturnItems,Long> {
}
