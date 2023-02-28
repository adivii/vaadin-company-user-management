package com.adivii.companymanagement.data.configuration.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.repository.UserRepository;
import com.adivii.companymanagement.data.service.security.CustomPasswordEncoder;

public class CustomUserDetailsImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomPasswordEncoder customPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<User> users = userRepository.findByEmailAddress(username);

        if(users.size() == 0) {
            throw new UsernameNotFoundException("Can't find user");
        }

        return new CustomUserDetails(users.get(0));
    }
}
