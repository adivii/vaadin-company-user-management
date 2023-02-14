package com.adivii.companymanagement.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adivii.companymanagement.data.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    
}
