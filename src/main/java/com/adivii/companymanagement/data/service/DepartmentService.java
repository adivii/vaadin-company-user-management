package com.adivii.companymanagement.data.service;

import java.util.List;

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

    public boolean saveDepartment(Department department) {
        if(department != null) {
            departmentRepository.save(department);

            return true;
        } else {
            return false;
        }
    }

    public boolean editData(Department department) {
        if(department != null){
            if(this.departmentRepository.findById(department.getDepartmentId()).isPresent()) {
                this.departmentRepository.save(department);
                return true;
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
