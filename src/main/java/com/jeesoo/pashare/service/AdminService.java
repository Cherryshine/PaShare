package com.jeesoo.pashare.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Value("${admin.password}")
    private String adminPassword;

    public boolean validatePassword(String password) {
        return adminPassword.equals(password);
    }
} 