package com.blogboard.server.service;

import com.blogboard.server.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.blogboard.server.data.entity.*;
import com.blogboard.server.data.repository.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.springframework.http.HttpStatus;

@Service
public class AccountService {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String LOGIN_PAGE = BASE_URL + File.separator + "login";

    //Custom Success/Error Messages
    private static final String ACCOUNT_CREATED = "Congrats, your account has successfully been " +
                                                                "created! You can now login and start blogging.";
    private static final String USERNAME_IN_USE = "Sorry, it seems there is already an account with that username";
    private static final String EMAIL_IN_USE = "Sorry, it seems there is already an account with that email";
    private static final String UNKNOWN_ERROR = "An unknown error has occurred.";


    /*
    * Method Name: Create Account
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicAPIResponse w/ HTTP Servlet BasicAPIResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public AccountServiceResponse createAccount(AccountRepository accountRepo, String username, String password,
        String email, HttpServletResponse httpResponse) throws IOException{
        AccountServiceResponse response = new AccountServiceResponse();

        //SUCCESS CASE: account with provided credentials doesn't already exist
        if (accountRepo.findByUsername(username) == null && accountRepo.findByEmail(email) == null) {
            Account newAccount = new Account(username, AppServiceHelper.hashString(password), email);
            Account savedAccount = accountRepo.save(newAccount);
            response.setMessage(ACCOUNT_CREATED);

            //FAILURE CASE: unexpected repository error
            if (savedAccount == null) {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                httpResponse.setHeader("Location", LOGIN_PAGE);
            }
        //FAILURE CASE(S): either account with same credential(s) already exists or unknown error occurred
        } else {
            httpResponse.setHeader("Location", LOGIN_PAGE);
            if(accountRepo.findByUsername(username) != null) {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, USERNAME_IN_USE);
            } else if (accountRepo.findByEmail(email) != null) {
                httpResponse.sendError(HttpServletResponse.SC_CONFLICT, EMAIL_IN_USE);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, UNKNOWN_ERROR);
            }
        }

        return response;
    }

    /*
    * Method Name: Update Email
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicAPIResponse w/ HTTP Servlet BasicAPIResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public AccountServiceResponse updateEmail() {
        return new AccountServiceResponse();
    }

    /*
    * Method Name: Update Password
    * Inputs: Account Repository, username, password, email, httpResponse
    * Return Value: Account Services BasicAPIResponse w/ HTTP Servlet BasicAPIResponse
    * Purpose: create new account, store in database and return the a success or failure message
     */

    public AccountServiceResponse updatePassword() {
        return new AccountServiceResponse();
    }
}
