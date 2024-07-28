package com.tech_hub.techhub.configuration;

import com.tech_hub.techhub.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;
    private final List<GrantedAuthority> authorities;
    private final boolean isEnabled;

    public CustomUserDetails(UserEntity userEntity) {
        super();
        this.userEntity = userEntity;
        authorities = Arrays.stream(userEntity.getRoles().getRoleName().split(","))
                .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        isEnabled = userEntity.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

}
