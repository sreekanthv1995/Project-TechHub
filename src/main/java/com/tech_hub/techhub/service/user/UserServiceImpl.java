package com.tech_hub.techhub.service.user;

import com.tech_hub.techhub.dto.UserDto;
//import com.tech_hub.TechHub.entity.PasswordResetToken;
import com.tech_hub.techhub.entity.Cart;
import com.tech_hub.techhub.entity.Role;
import com.tech_hub.techhub.entity.UserEntity;
import com.tech_hub.techhub.exception.PasswordChangeException;
import com.tech_hub.techhub.repository.RoleRepository;
//import com.tech_hub.TechHub.repository.TokenRepository;
import com.tech_hub.techhub.repository.TokenRepository;
import com.tech_hub.techhub.repository.UserRepository;


import com.tech_hub.techhub.util.EmailUtil;
import com.tech_hub.techhub.util.OtpUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Data
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;
    int flag = 0;
    @Autowired
    private OtpUtil otpUtil;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    public boolean verifyAccount(UserDto userDto){
        UserEntity user = userRepository.findByEmail(userDto.getEmail()).orElseThrow(()
                ->new RuntimeException("User Not found with this email"));

        if (user.getOtp().equals(userDto.getOtp()) && Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds()< (30*60)){
            user.setVerified(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
       return userRepository.findByEmail(email);
    }

    @Override
    public void deleteCart(Cart cart) {
        UserEntity user = userRepository.findById(cart.getUser().getId()).orElse(null);
        assert user != null;
        user.setCart(null);
        userRepository.save(user);

    }
    @Override
    public Page<UserEntity> findAllPage(int pageNo, int pageSize, String field, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNo-1,pageSize,sort);

        return userRepository.findAll(pageable);
    }
    @Override
    public void changePassword(String username, String currentPassword, String newPass) throws PasswordChangeException {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            if (passwordEncoder.matches(currentPassword,user.getPassword())){
                String newPassword = passwordEncoder.encode(newPass);
                user.setPassword(newPassword);
                userRepository.save(user);
            }else {
                throw new PasswordChangeException("password incorrect");
            }
        }else {
            throw new UsernameNotFoundException("user not found");
        }
    }

    @Override
    public boolean isVerified(String name) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(name);
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
            return user.isVerified();
        }
        return false;
    }

    @Override
    public boolean addUser(UserDto userDto) {
        try {
        Optional<UserEntity> userByUserName = userRepository.findByUsername(userDto.getUsername());
        Optional<UserEntity> userByEmail = userRepository.findByEmail(userDto.getEmail());

        if (userByUserName.isPresent() || userByEmail.isPresent()) {
            this.flag = 1;
            return false;
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())){
            return false;
        }
        String otp = otpUtil.generateOtp();

            emailUtil.sentOtp(userDto.getEmail(),otp);

            UserEntity newUser = new UserEntity();
            newUser.setFirstName(userDto.getFirstName());
            newUser.setLastName(userDto.getLastName());
            newUser.setUsername(userDto.getUsername());
            newUser.setEmail(userDto.getEmail());
            newUser.setPhoneNumber(userDto.getPhoneNumber());
            newUser.setPassword(encoder.encode(userDto.getPassword()));
            newUser.setOtp(otp);
            newUser.setOtpGeneratedTime(LocalDateTime.now());
            newUser.setEnabled(true);
            Role role = roleRepository.findByRoleName("ROLE_USER");
            newUser.setRoles(role);
            userRepository.save(newUser);
            return true;
        }catch (MessagingException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void save(UserEntity user) {
        userRepository.save(user);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public void updateUser(Long id, UserEntity updateUser) {
        Optional<UserEntity> getUser = userRepository.findById(id);
        if (getUser.isPresent()) {
            UserEntity theUser = getUser.get();
            theUser.setFirstName(updateUser.getFirstName());
            theUser.setLastName(updateUser.getLastName());
            theUser.setUsername(updateUser.getUsername());
            theUser.setEmail(updateUser.getEmail());
            theUser.setPhoneNumber(updateUser.getPhoneNumber());

        userRepository.save(theUser);

        }else {
            throw new UsernameNotFoundException("user not found");
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Override
    public UserEntity findByPhoneNumber(Long phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }
    @Override
    public void blockUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("User Not Found With Id"+id));
        user.setEnabled(false);
        userRepository.save(user);
    }
    @Override
    public void unBlockUser(Long id) {
        UserEntity user = userRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("User Not Found With Id"+id));
        user.setEnabled(true);
        userRepository.save(user);
    }

}
