package com.adivii.companymanagement.data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.repository.RoleRepository;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }
}
