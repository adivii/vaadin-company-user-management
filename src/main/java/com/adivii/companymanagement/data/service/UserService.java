package com.adivii.companymanagement.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.User;
import com.adivii.companymanagement.data.repository.UserRepository;

@Service
@Transactional
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    public List<User> getByEmail(String email) {
        return this.userRepository.findByEmailAddress(email);
    }

    public boolean saveUser(User user) {
        if (user != null) {
            this.userRepository.save(user);
            
            return true;
        } else {
            return false;
        }
    }

    public boolean editData(User user) {
        if(user != null){
            if(this.userRepository.findById(user.getUserId()).isPresent()) {
                this.userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }
}
