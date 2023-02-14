package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    public List<Department> findByCompanyId(Company company);
}
