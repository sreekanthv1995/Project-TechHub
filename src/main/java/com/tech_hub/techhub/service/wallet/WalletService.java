package com.tech_hub.techhub.service.wallet;

import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.entity.Wallet;

import java.util.Optional;

public interface WalletService {

    Wallet createWallet(UserEntity user);
    void save(Wallet wallet);
    Optional<Wallet> getWallet(UserEntity user);

}
