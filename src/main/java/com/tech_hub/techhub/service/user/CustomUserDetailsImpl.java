package com.tech_hub.techhub.service.user;

import com.tech_hub.techhub.configuration.CustomUserDetails;
import com.tech_hub.techhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

            CustomUserDetails user = userRepository.findByUsername(username)
                    .map(CustomUserDetails::new)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("Not Found"));
            if (!user.isEnabled()) {
                throw new DisabledException("Your entry is restricted...!Please contact the administrator.");
        }
            return user;
    }
}


