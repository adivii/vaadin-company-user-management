package com.adivii.companymanagement.data.service.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.service.AccountService;
import com.adivii.companymanagement.data.service.RoleMapService;
import com.adivii.companymanagement.data.service.UserService;

@Service
@Transactional
public class UserAuthService {

    // Service
    private UserService userService;
    private AccountService accountService;
    private RoleMapService roleMapService;

    // Additional Service
    private CustomPasswordEncoder customPasswordEncoder;

    public UserAuthService(UserService userService, AccountService accountService, RoleMapService roleMapService) {
        this.userService = userService;
        this.accountService = accountService;
        this.roleMapService = roleMapService;

        this.customPasswordEncoder = new CustomPasswordEncoder();
    }

    public User authenticateLogin(String username, String password) {
        List<Account> accounts = accountService.getByEmail(username);

        if (customPasswordEncoder.matches(password, accounts.get(0).getPassword())) {
            return userService.getByEmail(username).get(0);
        } else {
            return null;
        }
    }

    public List<String> getAuthorities(User user) {
        List<RoleMap> maps = roleMapService.getByEmail(user.getEmail());
        List<String> authorities = new ArrayList<>();

        for (RoleMap roleMap : maps) {
            authorities.add(roleMap.getRole().getValue());
        }

        return authorities;
    }

}
