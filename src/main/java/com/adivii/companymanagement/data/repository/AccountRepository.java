package com.adivii.companymanagement.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adivii.companymanagement.data.entity.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    public List<Account> findByEmailAddress(String email);
}
