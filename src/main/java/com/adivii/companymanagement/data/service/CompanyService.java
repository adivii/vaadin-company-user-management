package com.adivii.companymanagement.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.repository.CompanyRepository;

@Service
@Transactional
public class CompanyService {
    private CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<Company> getAllCompany() {
        return this.companyRepository.findAll();
    }

    public Long getCompanyCount() {
        return this.companyRepository.count();
    }

    public boolean addCompany(Company company) {
        if(company != null) {
            this.companyRepository.save(company);
            return true;
        } else {
            return false;
        }
    }

    public boolean editData(Company company) {
        if(company != null){
            if(this.companyRepository.findById(company.getCompanyId()).isPresent()) {
                this.companyRepository.save(company);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean deleteCompany(Company company) {
        if(company.getUserCount() == 0 && company.getDepartmentCount() == 0){
            this.companyRepository.delete(company);

            return true;
        } else {
            return false;
        }
    }
}
