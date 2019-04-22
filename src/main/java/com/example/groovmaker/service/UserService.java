package com.example.groovmaker.service;

import com.example.groovmaker.model.User;

public interface UserService {

    User findUserByEmail(String email);

    User saveUser(User user);

    User findUserById(int id);

    void followUser(User currentUser, int userIdToFollow);

    void unfollowUser(User currentUser, int userIdToUnfollow);

}
