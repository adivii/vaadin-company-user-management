package com.adivii.companymanagement.data.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adivii.companymanagement.data.entity.Account;
import com.adivii.companymanagement.data.repository.AccountRepository;
import com.adivii.companymanagement.data.repository.UserRepository;

@Service
@Transactional
public class AccountService {

    private AccountRepository accountRepository;
    private UserRepository userRepository;

    public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    public List<Account> getByEmail(String email) {
        return this.accountRepository.findByEmailAddress(email);
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
            if (this.getByEmail(account.getEmailAddress()).size() > 0) {
                Account currentAccount = this.getByEmail(account.getEmailAddress()).get(0);
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

    public ErrorService delete(Account account) {
        if (userRepository.findByEmail(account.getEmailAddress()).size() > 0) {
            return new ErrorService(true, "Can't Delete Record");
        } else {
            accountRepository.delete(account);
            return new ErrorService(false, null);
        }
    }
}
