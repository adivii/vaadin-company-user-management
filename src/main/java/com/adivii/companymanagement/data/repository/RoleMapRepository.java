package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.Role;
import com.adivii.companymanagement.data.entity.RoleMap;

public interface RoleMapRepository extends JpaRepository<RoleMap, Integer> {
    // Get By Email from Foreign Key User
    List<RoleMap> findByUserEmail(String email);
    List<RoleMap> findByUserEmailAndRole(String email, Role role);
    List<RoleMap> findByUserEmailAndRoleAndCompanyAndDepartment(String email, Role role, Company company, Department department);
    List<RoleMap> findByUserEmailAndCompany(String email, Company company);
    List<RoleMap> findByCompany(Company company);
}
