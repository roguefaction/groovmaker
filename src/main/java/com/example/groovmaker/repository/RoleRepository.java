package com.example.groovmaker.repository;

import com.example.groovmaker.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository//("roleRepository")
public interface RoleRepository extends JpaRepository <Role, Integer> {

    Role findByRole (String role); //default method for locating roles

}
