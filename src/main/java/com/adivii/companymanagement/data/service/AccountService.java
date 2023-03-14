package com.adivii.companymanagement.data.service;

import java.lang.StackWalker.Option;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> get(String email) {
        return this.accountRepository.findById(email);
    }

    public ErrorService save(Account account) {
        if (!account.getEmailAddress().isBlank() && !account.getPassword().isBlank()) {
            if (this.accountRepository.findByEmailAddress(account.getEmailAddress()).size() == 0) {
                this.accountRepository.save(account);
                return new ErrorService(false, null);
            } else {
                return new ErrorService(true, "Email Already Registered");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }

    public ErrorService update(Account account) {
        if (!account.getEmailAddress().isBlank() && !account.getPassword().isBlank()) {
            if (this.get(account.getEmailAddress()).isPresent()) {
                Account currentAccount = this.get(account.getEmailAddress()).get();
                if (this.accountRepository.findByEmailAddress(account.getEmailAddress()).size() == 0
                        || (account.getEmailAddress().equals(currentAccount.getEmailAddress()))) {
                    this.accountRepository.save(account);
                    return new ErrorService(false, null);
                } else {
                    return new ErrorService(true, "Email Already Registered");
                }
            } else {
                return new ErrorService(true, "Can't Find Account");
            }
        } else {
            return new ErrorService(true, "Field Can't Be Empty");
        }
    }
}
