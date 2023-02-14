package com.adivii.companymanagement.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adivii.companymanagement.data.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    
}
