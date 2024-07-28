package com.tech_hub.techhub.service.address;

import com.tech_hub.techhub.dto.AddressDto;
import com.tech_hub.techhub.entity.Address;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.exception.AddressNotFoundException;
import com.tech_hub.techhub.repository.AddressRepository;
import com.tech_hub.techhub.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    UserService userService;
    public String getCurrentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
    @Override
    public void saveAddress(Address address) {
        addressRepository.save(address);
    }

    @Override
    public void addNewAddress(AddressDto addressDto,UserEntity user) {
        Address address = new Address();

        address.setId(addressDto.getId());
        address.setHouseNumberOrName(addressDto.getHouseNumberOrName());
        address.setArea(addressDto.getArea());
        address.setCity(addressDto.getCity());
        address.setPinCode(addressDto.getPinCode());
        address.setState(addressDto.getState());
        address.setLandmark(addressDto.getLandmark());
        address.setCreatedAt(LocalDate.now());
        address.setDelete(false);
        address.setUser(user);
        addressRepository.save(address);
    }

    @Override
    public Optional<Address> findById(Integer id) {
        return addressRepository.findById(id);
    }
    @Override
    public List<Address> findByUser(UserEntity user) {
        return addressRepository.findByUser(user);
    }
    @Override
    public void deleteUserAddress(String username,Integer addressId) {
        UserEntity user = userService.findByUsername(username).orElse(null);
        if (user != null){
            Address addressToDelete = user.getAddresses().stream().filter(address ->
                    address.getId().equals(addressId)).findFirst().orElse(null);
            if (addressToDelete != null){
                user.getAddresses().remove(addressToDelete);
                userService.save(user);
                addressRepository.delete(addressToDelete);
            }
        }
    }

    @Override
    public void editAddress(Integer id, AddressDto addressDto) {
        Optional<Address> optionalAddress = addressRepository.findById(id);
        try {
            if (optionalAddress.isPresent()) {
                Address userAddress = optionalAddress.get();
                userAddress.setHouseNumberOrName(addressDto.getHouseNumberOrName());
                userAddress.setArea(addressDto.getArea());
                userAddress.setCity(addressDto.getCity());
                userAddress.setState(addressDto.getState());
                userAddress.setPinCode(addressDto.getPinCode());
                userAddress.setLandmark(addressDto.getLandmark());
                addressRepository.save(userAddress);
            } else {
                throw new AddressNotFoundException("Address not found");
            }
        }catch (AddressNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteAddressById(Integer id) {
        addressRepository.deleteById(id);
    }

    @Override
    public void deleteAddress(Address address) {
        addressRepository.delete(address);
    }

    @Override
    public List<Address> findAllUserAddresses(UserEntity user) {
        return addressRepository.findByUser(user);
    }
}
