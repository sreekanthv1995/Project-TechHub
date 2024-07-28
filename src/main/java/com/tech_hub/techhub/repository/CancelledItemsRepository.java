package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.CancelledItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancelledItemsRepository extends JpaRepository<CancelledItems,Long> {
}
