package com.tech_hub.techhub.service.user;

import com.tech_hub.techhub.dto.UserDto;
import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.exception.PasswordChangeException;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface UserService{

    boolean addUser(UserDto userDto);
    void save(UserEntity user);
    List<UserEntity> findAll();
    Optional<UserEntity> findById(Long id);
    void updateUser(Long id, UserEntity updatedUser);
    Optional<UserEntity> findByUsername(String username);
    UserEntity findByPhoneNumber(Long phoneNumber);
    void blockUser(Long id);
    void unBlockUser(Long id);
    boolean verifyAccount(UserDto userDto);
    Optional<UserEntity> findByEmail(String email);
    void deleteCart(Cart cart);
    Page<UserEntity>findAllPage(int pageNo,int pageSize,String field,String direction);
    void changePassword(String username,String currentPassword,String newPassword) throws PasswordChangeException;
    boolean isVerified(String name);
}
