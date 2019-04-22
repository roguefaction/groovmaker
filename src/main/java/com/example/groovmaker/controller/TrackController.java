package com.example.groovmaker.controller;

import com.example.groovmaker.exception.StorageFileNotFoundException;
import com.example.groovmaker.model.Playlist;
import com.example.groovmaker.model.Rating;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TrackController {

    private TrackService trackService;
    private UserService userService;
    private StorageService storageService;
    private HibernateSearchService searchservice;
    private CommentService commentService;
    private PlaylistService playlistService;
    private RatingService ratingService;

    @Autowired
    public TrackController(TrackService trackService, UserService userService, StorageService storageService, HibernateSearchService searchservice, CommentService commentService, PlaylistService playlistService, RatingService ratingService) {
        this.trackService = trackService;
        this.userService = userService;
        this.storageService = storageService;
        this.searchservice = searchservice;
        this.commentService = commentService;
        this.playlistService = playlistService;
        this.ratingService = ratingService;
    }

    @GetMapping(value = "/track/{id}")
    public ModelAndView getTrackById(@PathVariable("id") int id) {
        Track track = trackService.getTrackById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("track/show");

        User uploader = userService.findUserById(track.getUploader().getId());

        User user = getAuthenticatedUser();

        List<Rating> trackRatings = ratingService.getTracksRating(id);

        boolean hasUserRatedThisTrack = false;

        for (Rating rating : trackRatings) {
            if (rating.getRatingUser().contains(user)) {
                hasUserRatedThisTrack = true;
            }
        }

        boolean isCreatorAlreadyFollowed = false;

        if (getAuthenticatedUser().getFollowing().contains(track.getUploader())) {
            isCreatorAlreadyFollowed = true;
        }
        modelAndView.addObject("followBool", isCreatorAlreadyFollowed);

        modelAndView.addObject("ratingBool", hasUserRatedThisTrack);
        modelAndView.addObject("comments", commentService.getTracksComments(id));
        modelAndView.addObject("user", user);
        modelAndView.addObject("track", track);
        modelAndView.addObject("trackRating", ratingService.getTracksAverageRating(track.getId()));

        List<Playlist> playlists = playlistService.getUsersPlaylists(getAuthenticatedUser());

        Iterator<Playlist> iterator = playlists.iterator();
        while (iterator.hasNext()) {
            Playlist currentPlaylist = iterator.next();
            if (track.getInPlaylist().contains(currentPlaylist)) {
                iterator.remove();
            }
        }

        modelAndView.addObject("playlists", playlists);
        modelAndView.addObject("uploader", uploader);
        return modelAndView;
    }

    @PostMapping(value = "/track/create")
    public ModelAndView createTrack(@Valid Track track, BindingResult bindingResult, @RequestParam(value = "file") MultipartFile file) {

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dateAndTimeInLA = ZonedDateTime.ofInstant(now, zoneId);

        track.setLastModified(dateAndTimeInLA);
        ModelAndView modelAndView = new ModelAndView();

        User user = getAuthenticatedUser();
        modelAndView.addObject(user);

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/create");
        } else {
            if (file.isEmpty()) {
                modelAndView.setViewName("track/create");
                modelAndView.addObject("fileMessage", "Please choose a file");
            } else {

                if (!checkFileType(file.getContentType())) {
                    modelAndView.setViewName("track/create");
                    modelAndView.addObject("fileMessage", "Uploaded files must be audio files");
                } else {
                    track.setFileUrl(file.getOriginalFilename());
                    track.setUploaderId(user.getId());
                    track.setUploader(user);
                    trackService.createTrack(track);
                    storageService.store(file);

                    ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                    newModelAndView.addObject("track", track);
                    return newModelAndView;
                }


            }

        }

        return modelAndView;
    }

    @GetMapping(value = "/track/{id}/delete")
    public String deleteTrackById(@PathVariable("id") int id) {

        User user = getAuthenticatedUser();

        Track track = trackService.getTrackById(id);

        if (user.getId() == track.getUploaderId())
            trackService.deleteTrackById(id);
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can delete this track");

        return "redirect:/tracks";
    }

    @GetMapping(value = "/genre/{genre}")
    public ModelAndView getGenrePage(@PathVariable("genre") String genre,
                                     @RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size) {
        ModelAndView modelAndView = new ModelAndView();
        User user = getAuthenticatedUser();

        List<Track> filteredTracks = trackService.getTracksByGenre(genre);


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> trackPage = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), filteredTracks);
        modelAndView.addObject("tracksPage", trackPage);

        modelAndView.addObject("genre", genre);
        modelAndView.setViewName("track/genre");

        int totalPages = trackPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("tracks", filteredTracks);

        modelAndView.addObject(user);
        return modelAndView;

    }

    @PostMapping(value = "/track/{id}")
    public ModelAndView updateTrack(@PathVariable("id") int id, @Valid Track track, BindingResult bindingResult, @RequestParam(value = "file", required = false) MultipartFile file) {

        User user = getAuthenticatedUser();

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dateAndTimeInLA = ZonedDateTime.ofInstant(now, zoneId);

        track.setLastModified(dateAndTimeInLA);

        Track checkTrack = trackService.getTrackById(id);
        if (user.getId() != checkTrack.getUploaderId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can edit this track");

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(user);

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("track/edit");

        } else {
            if (file.isEmpty()) {
                trackService.updateTrackById(id, track);
                ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                newModelAndView.addObject(track);
                return newModelAndView;

            } else {

                if (!checkFileType(file.getContentType())) {
                    modelAndView.setViewName("track/create");
                    modelAndView.addObject("fileMessage", "Uploaded files must be audio files");
                } else {
                    track.setFileUrl(file.getOriginalFilename());
                    storageService.store(file);
                    trackService.updateTrackById(id, track);

                    ModelAndView newModelAndView = new ModelAndView("redirect:/track/" + track.getId());
                    newModelAndView.addObject(track);
                    return newModelAndView;
                }

            }

        }
        return modelAndView;

    }

    @GetMapping(value = "/track/create")
    public ModelAndView createTrackPage() {

        User user = getAuthenticatedUser();

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("track", new Track());
        modelAndView.addObject(user);
        modelAndView.setViewName("track/create");
        return modelAndView;
    }

    @GetMapping(value = "/genres")
    public ModelAndView getGenresPage() {

        User user = getAuthenticatedUser();

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(user);
        modelAndView.setViewName("track/genres");
        return modelAndView;
    }

    @GetMapping(value = "/track/{id}/edit")
    public ModelAndView editTrackPage(@PathVariable("id") int id) {

        User user = getAuthenticatedUser();

        Track track = trackService.getTrackById(id);
        track.setUploaderId(user.getId());

        trackService.getTrackById(id);

        // welcome objects added to displaying page
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject(track);
        modelAndView.addObject(user);
        modelAndView.setViewName("track/edit");
        return modelAndView;
    }


    @GetMapping(value = "/tracks")
    public ModelAndView getAllTracks(@RequestParam("page") Optional<Integer> page,
                                     @RequestParam("size") Optional<Integer> size) {
        ModelAndView modelAndView = new ModelAndView();
        User user = getAuthenticatedUser();

        List<Track> listOfTracks = trackService.getAllTracks();


        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> trackPage = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), listOfTracks);
        modelAndView.addObject("tracksPage", trackPage);

        modelAndView.setViewName("track/list");
        modelAndView.addObject(user);

        int totalPages = trackPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
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


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }

    private boolean checkFileType(String fileType) {
        List<String> alowedFileTypes = new ArrayList<>();
        alowedFileTypes.add(0, "audio/mp3");
        alowedFileTypes.add(1, "audio/flac");
        alowedFileTypes.add(2, "audio/wav");

        int matchedCases = 0;

        for (String item : alowedFileTypes) {
            if (item.equals(fileType))
                matchedCases++;
        }

        return matchedCases > 0;
    }

    @GetMapping(value = "/search")
    public ModelAndView search(@RequestParam(value = "search", required = false) String q,
                               @RequestParam("page") Optional<Integer> page,
                               @RequestParam("size") Optional<Integer> size) {

        ModelAndView modelAndView = new ModelAndView();
        List<Track> searchResults = null;
        try {
            trackService.getAllTracks();
            searchResults = searchservice.fuzzySearch(q);

        } catch (Exception ex) {
            // here you should handle unexpected errors
            // ...
            // throw ex;
        }

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> trackPage = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), searchResults);
        modelAndView.addObject("tracksPage", trackPage);

        User user = getAuthenticatedUser();

        int totalPages = trackPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }

        modelAndView.setViewName("track/list");
        modelAndView.addObject(user);
        modelAndView.addObject("tracks", searchResults);

        return modelAndView;

    }

}


