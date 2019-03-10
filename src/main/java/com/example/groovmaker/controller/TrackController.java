package com.example.groovmaker.controller;

import com.example.groovmaker.exception.StorageFileNotFoundException;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.StorageService;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class TrackController {


    private TrackService trackService;
    private UserService userService;
    private StorageService storageService;


    @Autowired
    public TrackController(TrackService trackService, UserService userService, StorageService storageService) {
        this.trackService = trackService;
        this.userService = userService;
        this.storageService = storageService;
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
    public ModelAndView createTrack(@Valid Track track, BindingResult bindingResult, @RequestParam(value = "file") MultipartFile file) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/create");
        } else {
            if (file.isEmpty()) {
                modelAndView.setViewName("track/create");
                modelAndView.addObject("fileMessage", "Please choose a file");
            } else {
                track.setFileUrl(file.getOriginalFilename());
                trackService.createTrack(track);
                storageService.store(file);

                ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                newModelAndView.addObject("track", track);
                return newModelAndView;
            }
        }
        return modelAndView;
    }

    @GetMapping(value = "/track/{id}/delete")
    public String deleteTrackById(@PathVariable("id") int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        Track track = trackService.getTrackById(id);

        if(user.getId() == track.getUploaderId())
            trackService.deleteTrackById(id);
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can delete this track");

        return "redirect:/tracks";
    }

    @PostMapping(value = "/track/{id}")
    public ModelAndView updateTrack(@PathVariable("id") int id, @Valid Track track, BindingResult bindingResult, @RequestParam(value = "file", required = false) MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        Track checkTrack = trackService.getTrackById(id);
        if(user.getId() != checkTrack.getUploaderId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can edit this track");

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/edit");

        } else {
            if (file.isEmpty()) {
                trackService.updateTrackById(id, track);
                ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                newModelAndView.addObject("track", track);
                return newModelAndView;

            } else {

                track.setFileUrl(file.getOriginalFilename());
                storageService.store(file);
                trackService.updateTrackById(id, track);

                ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                newModelAndView.addObject("track", track);
                return newModelAndView;

            }

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
    public ModelAndView getAllTracks() {
        ModelAndView modelAndView = new ModelAndView();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(authentication.getName());

        List<Track> listOfTracks = trackService.getAllTracks();

        modelAndView.setViewName("track/list");
        modelAndView.addObject("WelcomeUser", "Welcome, " + user.getName());
        modelAndView.addObject("tracks", listOfTracks);

        return modelAndView;
    }

    @GetMapping("/track/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


}


