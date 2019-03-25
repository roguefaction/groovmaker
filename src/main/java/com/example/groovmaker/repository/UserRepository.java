package com.example.groovmaker.repository;

import com.example.groovmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail (String email);  //default method for locating users
    User findById (String email);

}
