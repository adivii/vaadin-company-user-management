package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adivii.companymanagement.data.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findByEmail(String email);
    // Find record by roleId (FK) > department (FK) > departmentId
    public List<User> findByRoleIdDepartmentDepartmentId(Integer departmentId);
    public List<User> findByRoleIdCompanyCompanyId(Integer companyId);
}
