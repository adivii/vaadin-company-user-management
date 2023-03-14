package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adivii.companymanagement.data.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    public List<User> findByEmail(String email);
}
