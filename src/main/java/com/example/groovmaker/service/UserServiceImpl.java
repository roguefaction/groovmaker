package com.example.groovmaker.service;

import com.example.groovmaker.model.Role;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.RoleRepository;
import com.example.groovmaker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {


    private final RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;  // used for raw password encoding


    @Autowired  // might need too include fields in this constructor
    public UserServiceImpl(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public User findUserById(int id) {
        Optional<User> foundUser = userRepository.findById(id);

        if (foundUser.isPresent()) {
            return foundUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User was not found");
        }

    }

    public void followUser(User currentUser, int userIdToFollow) {

        Optional<User> foundUser = userRepository.findById(userIdToFollow);

        if (foundUser.isPresent()) {
            foundUser.get().getFollowers().add(currentUser);
            currentUser.getFollowing().add(foundUser.get());

            userRepository.save(currentUser);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }


    }

    public void unfollowUser(User currentUser, int userToUnfollow) {

        Optional<User> foundUser = userRepository.findById(userToUnfollow);

        if (foundUser.isPresent()) {
            User testuser = foundUser.get();
            if (testuser.getFollowers().contains(currentUser)) {
                testuser.getFollowers().remove(currentUser);
                userRepository.save(testuser);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not following this user");
            }

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }



}
