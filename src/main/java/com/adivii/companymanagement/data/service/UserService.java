package com.adivii.companymanagement.data.service;

import java.util.List;
import java.util.Optional;

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

    public ErrorService saveUser(User user) {
        ErrorService errorService = new ErrorService();
        if (user != null && !user.checkEmpty()) {
            if(getByEmail(user.getEmailAddress()).size() == 0){
                this.userRepository.save(user);
            
                errorService.setErrorStatus(false);
            } else {
                errorService.setErrorStatus(true);
                errorService.setErrorMessage("Email Already Registered");
            }
        } else {
            errorService.setErrorStatus(true);
            errorService.setErrorMessage("Field Can't Be Empty");
        }

        return errorService;
    }

    public boolean editData(User user) {
        if(user != null && !user.checkEmpty()){
            Optional<User> currentData = this.userRepository.findById(user.getUserId());
            if(currentData.isPresent()) {
                if((currentData.get().getEmailAddress().equals(user.getEmailAddress())) || getByEmail(user.getEmailAddress()).size() == 0){
                    this.userRepository.save(user);
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

    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }
}
