package com.adivii.companymanagement.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.repository.RoleMapRepository;

@Service
@Transactional
public class RoleMapService {
    
    @Autowired
    private RoleMapRepository roleMapRepository;

    public RoleMapService(RoleMapRepository roleMapRepository) {
        this.roleMapRepository = roleMapRepository;
    }
    
    public List<RoleMap> getByEmail(String email) {
        return this.roleMapRepository.findByUserEmail(email);
    }
}
