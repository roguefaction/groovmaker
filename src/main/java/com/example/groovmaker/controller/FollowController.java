package com.example.groovmaker.controller;

import com.example.groovmaker.model.User;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FollowController {

    private TrackService trackService;
    private UserService userService;

    @Autowired
    public FollowController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }

    @PostMapping(value = "/follow/{userId}")
    private String followUser(@PathVariable("userId") int userId, HttpServletRequest request) {

        userService.followUser(getAuthenticatedUser(), userId);
        return "redirect:/user/" + userId + "/profile";

    }

    @PostMapping(value = "/unfollow/{userId}")
    private String unfollowUser(@PathVariable("userId") int userId, HttpServletRequest request) {

        userService.unfollowUser(getAuthenticatedUser(), userId);
        return "redirect:/user/" + userId + "/profile";

    }


}
