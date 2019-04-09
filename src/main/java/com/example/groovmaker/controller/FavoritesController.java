package com.example.groovmaker.controller;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.*;
import com.example.groovmaker.utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class FavoritesController {


    private TrackService trackService;
    private UserService userService;

    @Autowired
    public FavoritesController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }

    @PostMapping(value = "/track/{id}/favorite")
    public String addToFavorites(@PathVariable("id") int id, HttpServletRequest request) {

        User user = getAuthenticatedUser();
        trackService.addToFavorites(id, user);

        return "redirect:" + request.getContextPath();
    }

    @PostMapping(value = "/track/{id}/unfavorite")
    public String removeFromFavorites(@PathVariable("id") int id, HttpServletRequest request) {

        User user = getAuthenticatedUser();
        trackService.removeFromFavorites(id, user);

        return "redirect:" + request.getContextPath();
    }

    @GetMapping(value = "/user/favorites")
    public ModelAndView getFavortiesPage(@RequestParam("page") Optional<Integer> page,
                                         @RequestParam("size") Optional<Integer> size) {

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("user/favorites");

        currentView.addObject("user", getAuthenticatedUser());
        currentView.addObject("userProfile", getAuthenticatedUser());


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> tracksPaginated = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), trackService.getAllFavorites(getAuthenticatedUser()));
        currentView.addObject("tracks", tracksPaginated);

        currentView = ControllerHelper.addPaginatedViews(currentView, tracksPaginated);
        return currentView;
    }

}
