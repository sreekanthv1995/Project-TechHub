package com.tech_hub.techhub.service.address;

import com.tech_hub.techhub.dto.AddressDto;
import com.tech_hub.techhub.entity.Address;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.exception.AddressNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    void saveAddress(Address address);
    void addNewAddress(AddressDto addressDto,UserEntity user);
    Optional<Address>findById(Integer id);
    List<Address>findByUser(UserEntity user);
    void deleteUserAddress(String username,Integer addressId);
    void editAddress(Integer id,AddressDto addressDto) throws AddressNotFoundException;
    void deleteAddressById(Integer id);
    void deleteAddress(Address address);
    List<Address>findAllUserAddresses(UserEntity user);

}
