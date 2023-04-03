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

@Service
@Transactional
public class UserAuthService {
    
    @Autowired
    private AccountService accountService;
    @Autowired
    private CustomPasswordEncoder customPasswordEncoder;
    @Autowired
    private RoleMapService roleMapService;

    public UserAuthService(AccountService accountService, CustomPasswordEncoder customPasswordEncoder,
            RoleMapService roleMapService) {
        this.accountService = accountService;
        this.customPasswordEncoder = customPasswordEncoder;
        this.roleMapService = roleMapService;
    }

    public Account authenticateLogin(String username, String password) {
        List<Account> accounts = accountService.getByEmail(username);

        if(customPasswordEncoder.matches(password, accounts.get(0).getPassword())) {
            return accounts.get(0);
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
