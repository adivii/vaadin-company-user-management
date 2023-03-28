package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.RoleMap;

public interface RoleMapRepository extends JpaRepository<RoleMap, Integer> {
    List<RoleMap> findByUserEmail(String email);
}
