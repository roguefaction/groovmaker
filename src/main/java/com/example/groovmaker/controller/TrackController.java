package com.example.groovmaker.controller;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class TrackController {


    private TrackService trackService;
    private UserService userService;

    @Autowired
    public TrackController(TrackService trackService, UserService userService) {
        this.trackService = trackService;
        this.userService = userService;
    }

    @GetMapping(value = "/track/{id}")
    public ModelAndView getTrackById(@PathVariable("id") int id) {
        Track track = trackService.getTrackById(id);
        ModelAndView modelAndView = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());

        modelAndView.setViewName("track/show");
        modelAndView.addObject("track", track);
        return modelAndView;
    }

    @PostMapping(value = "/track/create")
    public ModelAndView createTrack(@Valid Track track, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/create");
        } else {
            trackService.createTrack(track);

            ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
            newModelAndView.addObject("track", track);
            return newModelAndView;

        }
        return modelAndView;
    }

    @GetMapping(value = "/track/{id}/delete")
    public ModelAndView deleteTrackById(@PathVariable("id") int id) {
        ModelAndView modelAndView = new ModelAndView();
        trackService.deleteTrackById(id);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.addObject("message", "The track has been successfully deleted");
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @PostMapping(value = "/track/{id}")
    public ModelAndView updateTrack(@PathVariable("id") int id, @Valid Track track, BindingResult bindingResult) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/edit");
        } else {
            Track updatedTrack = trackService.updateTrackById(id, track);

            ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + updatedTrack.getId());
            newModelAndView.addObject("track", updatedTrack);
            return newModelAndView;
        }
        return modelAndView;
    }

    @GetMapping(value = "/track/create")
    public ModelAndView createTrackPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("track", new Track());
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.setViewName("track/create");
        return modelAndView;
    }

    @GetMapping(value = "/track/{id}/edit")
    public ModelAndView editTrackPage(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        Track track = trackService.getTrackById(id);


        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("track", track);
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.setViewName("track/edit");
        return modelAndView;
    }


    @GetMapping(value = "/tracks")
    public ModelAndView getAllTracks(){
        ModelAndView modelAndView = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        List<Track> listOfTracks = trackService.getAllTracks();

        modelAndView.setViewName("track/list");
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.addObject("tracks", listOfTracks);

        return modelAndView;
    }


}


