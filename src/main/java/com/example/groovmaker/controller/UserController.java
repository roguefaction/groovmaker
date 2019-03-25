package com.example.groovmaker.controller;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {


    private UserService userService;
    private TrackService trackService;

    @Autowired
    public UserController(UserService userService, TrackService trackService) {
        this.userService = userService;
        this.trackService = trackService;
    }

    @GetMapping(value = "/user/home")
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();

        // get current users name from auth object
        User user = getAuth();

        // welcome objects added to displaying page
        modelAndView.addObject(user);
        modelAndView.addObject("userMessage", "Content Available Only for Users with User Role");

        modelAndView.setViewName("user/home");
        return modelAndView;
    }

    @GetMapping(value = "/user/{id}/profile")
    public ModelAndView userProfileById(@PathVariable("id") int id, Authentication authentication) {
        ModelAndView modelAndView = new ModelAndView();

        User user = getAuth();
        modelAndView.addObject(user);

        User userProfile = userService.findUserById(id);
        List<Track> userTracks = trackService.findByUploaderId(userProfile.getId());

        modelAndView.addObject("tracks", userTracks);
        modelAndView.addObject("userProfile", userProfile);

        modelAndView.setViewName("user/profile");
        return modelAndView;
    }


    private User getAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }
}
