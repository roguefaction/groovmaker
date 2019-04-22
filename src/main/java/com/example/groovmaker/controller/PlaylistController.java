package com.example.groovmaker.controller;

import com.example.groovmaker.model.Playlist;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.PlaylistService;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import com.example.groovmaker.utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class PlaylistController {

    private PlaylistService playlistService;
    private UserService userService;
    private TrackService trackService;

    @Autowired
    public PlaylistController(PlaylistService playlistService, UserService userService, TrackService trackService) {
        this.playlistService = playlistService;
        this.userService = userService;
        this.trackService = trackService;
    }

    @PostMapping(value = "/track/{id}/addtoplaylist")
    private String addTrackToPlaylist(@PathVariable("id") int id, @RequestParam("playlist") int playlist, HttpServletRequest request) {

        playlistService.addTrackToPlaylist(id, playlist);
        return "redirect:" + request.getContextPath();
    }

    @PostMapping(value = "/track/{id}/removefromplaylist/{playlistId}")
    private String removeTrackFromPlaylist(@PathVariable("id") int id, @PathVariable("playlistId") int playlistId, HttpServletRequest request) {

        playlistService.removeTrackFromPlaylist(id, playlistId);
        return "redirect:/playlist/" + playlistId;
    }


    @GetMapping(value = "/playlist/{id}")
    public ModelAndView getPlaylistById(@PathVariable("id") int id,
                                        @RequestParam("page") Optional<Integer> page,
                                        @RequestParam("size") Optional<Integer> size) {

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("playlist/show");

        currentView.addObject("user", getAuthenticatedUser());

        Playlist currentPlaylist = playlistService.getPlaylistById(id);

        boolean isCreatorAlreadyFollowed = false;

        if (getAuthenticatedUser().getFollowing().contains(currentPlaylist.getCreator())) {
            isCreatorAlreadyFollowed = true;
        }
        currentView.addObject("followBool", isCreatorAlreadyFollowed);

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Track> tracksPaginated = trackService.findPaginatedTracks(PageRequest.of(currentPage - 1, pageSize), playlistService.getPlaylistsTracks(currentPlaylist));
        currentView.addObject("tracks", tracksPaginated);
        currentView = ControllerHelper.addPaginatedViews(currentView, tracksPaginated);


        currentView.addObject("playlist", currentPlaylist);
        currentView.addObject("creator", currentPlaylist.getCreator());
        return currentView;
    }

    @PostMapping(value = "/playlist/create")
    public ModelAndView createPlaylist(@Valid Playlist playlist, BindingResult bindingResult) {

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("playlist/create");

        currentView.addObject("user", getAuthenticatedUser());

        if (bindingResult.hasErrors()) {
            currentView.setViewName("playlist/create");
        } else {
            playlist.setCreator(getAuthenticatedUser());
            Playlist savedPlaylist = playlistService.createPlaylist(playlist);

            currentView.setViewName("redirect:/playlist/" + savedPlaylist.getId());
        }
        return currentView;
    }

    @PostMapping(value = "/playlist/{id}/delete")
    public String deletePlaylistById(@PathVariable("id") int id, HttpServletRequest request) {

        Playlist playlist = playlistService.getPlaylistById(id);

        if (getAuthenticatedUser().getId() == playlist.getCreator().getId())
            playlistService.deletePlaylistById(id);
        else
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only creator user can delete this playlist");

        return "redirect:/user/" + getAuthenticatedUser().getId() + "/playlists";
    }

    @PostMapping(value = "/playlist/{id}/edit")
    public ModelAndView updatePlaylistById(@PathVariable("id") int id, @Valid Playlist playlist, BindingResult bindingResult) {

        if (getAuthenticatedUser().getId() != playlistService.getPlaylistById(id).getCreator().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can edit this track");

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("playlist/edit");

        if (bindingResult.hasErrors()) {
            currentView.setViewName("playlist/edit");
        } else {
            playlistService.updatePlaylistById(id, playlist);
            currentView.setViewName("redirect:/playlist/" + id);
        }

        return currentView;
    }

    @GetMapping(value = "/playlist/create")
    public ModelAndView getCreatePlaylistPage() {

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("playlist/create");

        currentView.addObject("playlist", new Playlist());
        currentView.addObject("user", getAuthenticatedUser());

        return currentView;
    }

    @GetMapping(value = "/playlist/{id}/edit")
    public ModelAndView getEditPlaylistPage(@PathVariable("id") int id) {

        Playlist playlistToEdit = playlistService.getPlaylistById(id);

        if (getAuthenticatedUser().getId() != playlistToEdit.getCreator().getId())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only uploading user can edit this track");

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("playlist/edit");

        currentView.addObject("playlist", playlistToEdit);
        currentView.addObject("user", getAuthenticatedUser());

        return currentView;
    }

    @GetMapping(value = "/user/{user_id}/playlists")
    public ModelAndView getUsersPlaylists(@PathVariable("user_id") int userId,
                                          @RequestParam("page") Optional<Integer> page,
                                          @RequestParam("size") Optional<Integer> size) {

        ModelAndView currentView = new ModelAndView();
        currentView.setViewName("user/playlists");

        User viewedUser = userService.findUserById(userId);
        currentView.addObject("userProfile", viewedUser);

        currentView.addObject("user", getAuthenticatedUser());

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(6);

        Page<Playlist> playlistsPaginated = playlistService.findPaginatedPlaylists(PageRequest.of(currentPage - 1, pageSize), playlistService.getUsersPlaylists(viewedUser));
        currentView.addObject("playlists", playlistsPaginated);

        return currentView;
    }


    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }
}
