package com.tech_hub.techhub.service.wallet;

import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.entity.Wallet;
import com.tech_hub.techhub.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    WalletRepository walletRepository;

    @Override
    public Wallet createWallet(UserEntity user) {
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setWalletAmount(0.0);
        walletRepository.save(wallet);

        return wallet;
    }

    @Override
    public void save(Wallet wallet) {
        walletRepository.save(wallet);
    }

    public Optional<Wallet> getWallet(UserEntity user) {
        return walletRepository.findByUser(user).or
                (() -> {
                    Wallet newWallet = createWallet(user);
                    return Optional.of(newWallet);
                });
    }
}
