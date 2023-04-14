package com.adivii.companymanagement.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.repository.CompanyRepository;
import com.adivii.companymanagement.data.repository.RoleMapRepository;

@Service
@Transactional
public class CompanyService {
    private CompanyRepository companyRepository;
    private RoleMapRepository roleMapRepository;

    public CompanyService(CompanyRepository companyRepository, RoleMapRepository roleMapRepository) {
        this.companyRepository = companyRepository;
        this.roleMapRepository = roleMapRepository;
    }

    public List<Company> getAllCompany() {
        return this.companyRepository.findAll();
    }

    public List<Company> getByName(String name) {
        return this.companyRepository.findByCompanyName(name);
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

    // public int getEmployeeCount(Company company) {
    //     int employeeCount = company.getUserCount();

    //     for (Company childCompany : getChildCompany(company)) {
    //         employeeCount += getEmployeeCount(childCompany);
    //     }

    //     return employeeCount;
    // }

    public ErrorService addCompany(Company company) {
        if(company != null && !company.checkEmpty()) {
            if(this.companyRepository.findByCompanyName(company.getCompanyName()).size() == 0) {
                this.companyRepository.save(company);
                return new ErrorService(false, null);
            } else {
                return new ErrorService(true, "Name Already Used");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public ErrorService editData(Company company) {
        if(company != null && !company.checkEmpty()){
            if(this.companyRepository.findById(company.getCompanyId()).isPresent()) {
                // TODO : Make more efficient method
                Company currentData = this.companyRepository.findById(company.getCompanyId()).get();
                if(currentData.getCompanyName().equals(company.getCompanyName()) || this.companyRepository.findByCompanyName(company.getCompanyName()).size() == 0) {
                    if (company.getChildCompanyCount() == 0 || company.getHoldingCompany() != null) {
                        this.companyRepository.save(company);
                        return new ErrorService(false, null);   
                    } else {
                        return new ErrorService(true, "Child Company Can't Have Another Child Company");
                    }
                } else {
                    return new ErrorService(true, "Name Already Registered");
                }
                
            } else {
                return new ErrorService(true, "Can't Find Record");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public boolean deleteCompany(Company company) {
        if(roleMapRepository.findByCompany(company).size() == 0 && company.getChildCompanyCount() == 0){
            this.companyRepository.delete(company);

            return true;
        } else {
            return false;
        }
    }
}
