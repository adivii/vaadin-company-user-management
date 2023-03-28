package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findByValue(String value);
}
