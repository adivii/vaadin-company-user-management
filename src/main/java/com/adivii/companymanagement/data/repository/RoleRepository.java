package com.adivii.companymanagement.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
}
