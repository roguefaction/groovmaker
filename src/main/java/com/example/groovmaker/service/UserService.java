package com.example.groovmaker.service;

import com.example.groovmaker.model.User;
import org.springframework.stereotype.Service;


public interface UserService {

    User findUserByEmail(String email);
    User saveUser(User user);

}
