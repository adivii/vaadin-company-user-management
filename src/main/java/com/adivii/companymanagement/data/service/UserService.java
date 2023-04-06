package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Company;
import com.adivii.companymanagement.data.entity.Department;
import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;
    
    // @Autowired
    // PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

    public ErrorService saveUser(User user) {
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

    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }

    // TODO: Fix User Validation Method
    // public ErrorService validateUser(User user) {
    //     if (user != null && !user.getEmailAddress().isBlank() && !user.getPassword().isBlank()) {
    //         List<User> userCheck = getByEmail(user.getEmailAddress());

    //         if(userCheck.size() == 0) {
    //             return new ErrorService(true, "Email Doesn't Registered");
    //         } else {
    //             if (BCrypt.checkpw(user.getPassword(), userCheck.get(0).getPassword())) {
    //                 return new ErrorService(false, null);
    //             } else {
    //                 return new ErrorService(true, "Password Invalid");
    //             }
    //         }
    //     } else {
    //         return new ErrorService(true, "Field Can't Be Empty");
    //     } 
    // }
}
