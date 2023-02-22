package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public List<User> getByEmail(String email) {
        return this.userRepository.findByEmailAddress(email);
    }

    public ErrorService saveUser(User user) {
        if (user != null && !user.checkEmpty()) {
            if(getByEmail(user.getEmailAddress()).size() == 0){
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
                if((currentData.get().getEmailAddress().equals(user.getEmailAddress())) || getByEmail(user.getEmailAddress()).size() == 0){
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

    public ErrorService validateUser(User user) {
        if (user != null && !user.getEmailAddress().isBlank() && !user.getPassword().isBlank()) {
            List<User> userCheck = getByEmail(user.getEmailAddress());

            if(userCheck.size() == 0) {
                return new ErrorService(true, "Email Doesn't Registered");
            } else {
                if (BCrypt.checkpw(user.getPassword(), userCheck.get(0).getPassword())) {
                    return new ErrorService(false, null);
                } else {
                    return new ErrorService(true, "Password Invalid");
                }
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        } 
    }
}
