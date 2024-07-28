package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.Address;
import com.tech_hub.techhub.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address>findByUser(UserEntity user);
}
