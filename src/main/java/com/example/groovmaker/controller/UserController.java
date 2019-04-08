package com.example.groovmaker.controller;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.example.groovmaker.utils.ControllerHelper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        User user = getAuthenticatedUser();

        // welcome objects added to displaying page
        modelAndView.addObject(user);
        modelAndView.addObject("userMessage", "Content Available Only for Users with User Role");

        modelAndView.setViewName("user/home");
        return modelAndView;
    }


    @GetMapping(value = "/user/{id}/profile")
    public ModelAndView userProfileById(@PathVariable("id") int id,
                                        Authentication authentication,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size) {

        ModelAndView modelAndView = new ModelAndView();
        User user = getAuthenticatedUser();

        User userProfile = userService.findUserById(id);
        List<Track> userTracks = trackService.findByUploaderId(userProfile.getId());


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> trackPage = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), userTracks);
        modelAndView.addObject("tracksPage", trackPage);

        modelAndView.addObject(user);

        int totalPages = trackPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("tracks", userTracks);

        modelAndView.addObject("userProfile", userProfile);

        modelAndView.setViewName("user/profile");
        return modelAndView;
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }
}
