package com.example.groovmaker.controller;

import com.example.groovmaker.model.Rating;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.RatingService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RatingController {

    private UserService userService;
    private RatingService ratingService;

    @Autowired
    public RatingController(UserService userService, RatingService ratingService) {
        this.userService = userService;
        this.ratingService = ratingService;
    }

    @PostMapping(value = "/track/{id}/rating")
    public String addRating(@PathVariable("id") int trackId, @Valid Rating rating, HttpServletRequest request) {
        ratingService.addRating(rating, getAuthenticatedUser(), trackId);
        return "redirect:" + request.getContextPath();

    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }

}
