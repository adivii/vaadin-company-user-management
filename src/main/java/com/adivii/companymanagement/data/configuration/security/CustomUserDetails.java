package com.adivii.companymanagement.data.configuration.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.adivii.companymanagement.data.entity.User;

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
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        // Set Authority for User
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // if(user.isActivated()){
        //     for (RoleMap role : user.getRoleId()) {
        //         authorities.add(new SimpleGrantedAuthority(role.getRole().getValue()));
        //     }
        // } else {
        //     authorities.add(new SimpleGrantedAuthority("unactivated"));
        // }

        // Temporary Solution
        authorities.add(new SimpleGrantedAuthority("authority"));

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
