package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.RoleMap;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.repository.AccountRepository;
import com.adivii.companymanagement.data.repository.RoleMapRepository;
import com.adivii.companymanagement.data.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;
    private RoleMapRepository roleMapRepository;
    private AccountRepository accountRepository;

    public UserService(UserRepository userRepository, RoleMapRepository roleMapRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.roleMapRepository = roleMapRepository;
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public Optional<User> getUser(Integer id) {
        return this.userRepository.findById(id);
    }

    public List<User> getByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public List<User> getByDepartment(Department department) {
        return this.userRepository.findByRoleIdDepartmentDepartmentId(department.getDepartmentId());
    }
    
    public List<User> getByCompany(Company company) {
        return this.userRepository.findByRoleIdCompanyCompanyId(company.getCompanyId());
    }

    private User cleanData(User user) {
        user.setAddress(user.getAddress().strip());
        user.setEmail(user.getEmail().strip());
        user.setFirstName(user.getFirstName().strip());
        user.setLastName(user.getLastName().strip());
        user.setPhoneNumber(user.getPhoneNumber().strip());

        return user;
    }

    public ErrorService saveUser(User user) {
        user = cleanData(user);
        if (user != null && !user.checkEmpty()) {
            if(getByEmail(user.getEmail()).size() == 0){
                user.setEnabled(true);
                this.userRepository.save(user);
            
                return new ErrorService(false, null);
            } else {
                return new ErrorService(true, "Email Already Registered");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public ErrorService editData(User user) {
        user = cleanData(user);
        if(user != null && !user.checkEmpty()){
            Optional<User> currentData = this.userRepository.findById(user.getUserId());
            if(currentData.isPresent()) {
                if((currentData.get().getEmail().equals(user.getEmail())) || getByEmail(user.getEmail()).size() == 0){
                    this.userRepository.save(user);
                    return new ErrorService(false, null);
                } else {
                    return new ErrorService(true, "Email Already Registered");
                }
            } else {
                return new ErrorService(true, "Can't Find Record");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public void deleteUser(User user, Company company) {
        for (RoleMap roleMap : roleMapRepository.findByUserEmailAndCompany(user.getEmail(), company)) {
            this.roleMapRepository.delete(roleMap);
        }

        if(accountRepository.findByEmailAddress(user.getEmail()).size() == 0) {
            this.userRepository.delete(user);
        }
    }
}
