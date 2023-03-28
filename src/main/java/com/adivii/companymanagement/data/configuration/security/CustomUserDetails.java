package com.adivii.companymanagement.data.configuration.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.repository.RoleMapRepository;
import com.adivii.companymanagement.data.service.RoleMapService;

public class CustomUserDetails implements UserDetails {
    private User user;

    // TODO: Autowired doesn't work
    // @Autowired
    // private RoleMapService roleMapService;

    int temp;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        // Set Authority for User
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleId().getRole().getValue()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getAccount().getPassword();
    }

    @Override
    public String getUsername() {
        return user.getAccount().getEmailAddress();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return user.isEnabled();
    }

    public User getUser() {
        return user;
    }
}
