package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adivii.companymanagement.data.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    public List<Company> findByCompanyName(String companyName);
    public List<Company> findByHoldingCompany(Company company);
}
