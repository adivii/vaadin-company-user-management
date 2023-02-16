package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.repository.DepartmentRepository;

@Service
public class DepartmentService {
    private DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartment() {
        return this.departmentRepository.findAll();
    }

    public List<Department> getByCompany(Company company) {
        return this.departmentRepository.findByCompanyId(company);
    }

    public List<String> getNameByCompany(List<Department> data) {
        return data.stream().map(Department::getName).collect(Collectors.toList());
    }

    public boolean saveDepartment(Department department) {
        if(department != null && !department.checkEmpty() && !getNameByCompany(this.departmentRepository.findByCompanyId(department.getCompanyId())).contains(department.getName())) {
            departmentRepository.save(department);

            return true;
        } else {
            return false;
        }
    }

    public boolean editData(Department department) {
        if(department != null && !department.checkEmpty()){
            if(this.departmentRepository.findById(department.getDepartmentId()).isPresent()) {
                // TODO : Make more efficient method to do checking
                Department currentData = this.departmentRepository.findById(department.getDepartmentId()).get();
                if((currentData.getName().equals(department.getName()) && currentData.getCompanyId().getCompanyId() == department.getCompanyId().getCompanyId())  || !getNameByCompany(this.departmentRepository.findByCompanyId(department.getCompanyId())).contains(department.getName())){
                    this.departmentRepository.save(department);
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

    public boolean deleteDepartment(Department department) {
        if(department.getUserCount() == 0){
            this.departmentRepository.delete(department);

            return true;
        } else {
            return false;
        }
    }
}
