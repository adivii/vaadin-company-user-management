package com.adivii.companymanagement.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Role;
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

    public List<RoleMap> getByEmailAndRole(String email, Role role) {
        return this.roleMapRepository.findByUserEmailAndRole(email, role);
    }
    
    public List<RoleMap> getByEmailAndRoleAndCompanyAndDepartment(String email, Role role, Company company, Department department) {
        return this.roleMapRepository.findByUserEmailAndRoleAndCompanyAndDepartment(email, role, company, department);
    }

    public List<RoleMap> getByEmailAndCompany(String email, Company company) {
        return this.roleMapRepository.findByUserEmailAndCompany(email, company);
    }

    public void add(RoleMap roleMap){
        this.roleMapRepository.save(roleMap);
    }

    public void delete(RoleMap roleMap){
        this.roleMapRepository.delete(roleMap);
    }
}
