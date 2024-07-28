package com.tech_hub.techhub.repository;

import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    Optional<Wallet> findByUser(UserEntity user);
}
