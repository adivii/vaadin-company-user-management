package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.repository.DepartmentRepository;
import com.adivii.companymanagement.data.repository.UserRepository;

@Service
public class DepartmentService {
    private DepartmentRepository departmentRepository;
    private UserRepository userRepository;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Department> getAllDepartment() {
        return this.departmentRepository.findAll();
    }

    public List<Department> getByName(String name) {
        return this.departmentRepository.findByName(name);
    }

    public List<Department> getByCompany(Company company) {
        return this.departmentRepository.findByCompanyId(company);
    }

    public List<String> getNameByCompany(List<Department> data) {
        return data.stream().map(Department::getName).collect(Collectors.toList());
    }

    public ErrorService saveDepartment(Department department) {
        if(department != null && !department.checkEmpty()) {
            if (!getNameByCompany(this.departmentRepository.findByCompanyId(department.getCompanyId())).contains(department.getName())) {
                departmentRepository.save(department);

                return new ErrorService(false, null);
            } else {
                return new ErrorService(true, "Name Already Registered");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public ErrorService editData(Department department) {
        if(department != null && !department.checkEmpty()){
            if(this.departmentRepository.findById(department.getDepartmentId()).isPresent()) {
                // TODO : Make more efficient method to do checking
                Department currentData = this.departmentRepository.findById(department.getDepartmentId()).get();
                if((currentData.getName().equals(department.getName()) && currentData.getCompanyId().getCompanyId() == department.getCompanyId().getCompanyId())  || !getNameByCompany(this.departmentRepository.findByCompanyId(department.getCompanyId())).contains(department.getName())){
                    this.departmentRepository.save(department);
                    return new ErrorService(false, null);
                } else {
                    return new ErrorService(true, "Company Already Have Department with That Name");
                }
            } else {
                return new ErrorService(true, "Can't Find Record");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public boolean deleteDepartment(Department department) {
        if(userRepository.findByRoleIdDepartmentDepartmentId(department.getDepartmentId()).size() == 0){
            this.departmentRepository.delete(department);

            return true;
        } else {
            return false;
        }
    }
}
