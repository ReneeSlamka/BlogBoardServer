package com.blogboard.server.service;

import com.blogboard.server.web.CreateAccountResponse;
import com.blogboard.server.web.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.Account;
import com.blogboard.server.data.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepo;

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepo = accountRepository;
    }


    public CreateAccountResponse createAccount(String username, String password, String email) {
        CreateAccountResponse createAccountResponse = new CreateAccountResponse();

        if(accountRepo.findByUsername(username) != null) {
            createAccountResponse.setToFailure("username");
        } else if (accountRepo.findByEmail(email) != null) {
            createAccountResponse.setToFailure("email");
        } else {
            Account newAccount = new Account(username, password, email);
            //set try catch here!!
            Account savedAccount = accountRepo.save(newAccount);
            createAccountResponse.setToSuccess();
        }

        return createAccountResponse;
    }

    public LoginResponse login(String username, String password) {
        LoginResponse loginResponse = new LoginResponse();

        if (accountRepo.findByUsername(username) == null) {
            loginResponse.setToFailure("username");
        } else if (!accountRepo.findByUsername(username).getPassword().equals(password)) {
            loginResponse.setToFailure("password");
        } else {
            loginResponse.setToSuccess();
        }

        return loginResponse;
    }

    public Account findOne(Long id) {
        Account account = accountRepo.findOne(id);
        return account;
    }

    public void delete(Long id) {
        accountRepo.delete(id);
    }

}
