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

    public List<Company> getHoldingCompany() {
        return this.companyRepository.findByHoldingCompany(null);
    }

    public List<Company> getChildCompany(Company holdingCompany) {
        return this.companyRepository.findByHoldingCompany(holdingCompany);
    }

    public int getEmployeeCount(Company company) {
        int employeeCount = company.getUserCount();

        for (Company childCompany : getChildCompany(company)) {
            employeeCount += getEmployeeCount(childCompany);
        }

        return employeeCount;
    }

    public boolean addCompany(Company company) {
        if(company != null && !company.checkEmpty() && this.companyRepository.findByCompanyName(company.getCompanyName()).size() == 0) {
            this.companyRepository.save(company);
            return true;
        } else {
            return false;
        }
    }

    public boolean editData(Company company) {
        if(company != null && !company.checkEmpty()){
            if(this.companyRepository.findById(company.getCompanyId()).isPresent()) {
                // TODO : Make more efficient method
                Company currentData = this.companyRepository.findById(company.getCompanyId()).get();
                if(currentData.getCompanyName().equals(company.getCompanyName()) || this.companyRepository.findByCompanyName(company.getCompanyName()).size() == 0) {
                    this.companyRepository.save(company);
                    return true;
                } else {
                    return false;
                }
                
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean deleteCompany(Company company) {
        if(company.getUserCount() == 0 && company.getDepartmentCount() == 0 && company.getChildCompanyCount() == 0){
            this.companyRepository.delete(company);

            return true;
        } else {
            return false;
        }
    }
}
