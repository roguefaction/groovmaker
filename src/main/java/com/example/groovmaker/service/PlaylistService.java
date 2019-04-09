package com.example.groovmaker.service;

import com.example.groovmaker.model.Playlist;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.PlaylistRepository;
import com.example.groovmaker.repository.TrackRepository;
import com.example.groovmaker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    private PlaylistRepository playlistRepository;
    private TrackRepository trackRepository;
    private UserRepository userRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, TrackRepository trackRepository, UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
    }

    public void addTrackToPlaylist(int trackId, int playlistId) {
        Optional<Track> foundTrack = trackRepository.findById(trackId);
        Optional<Playlist> foundPlaylist = playlistRepository.findById(playlistId);

        if (foundTrack.isPresent() && foundPlaylist.isPresent()) {
            if (foundPlaylist.get().getTracks().contains(foundTrack.get())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are have already added this track to this playlist");
            }
            foundTrack.get().getInPlaylist().add(foundPlaylist.get());
            foundPlaylist.get().getTracks().add(foundTrack.get());
            playlistRepository.save(foundPlaylist.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist or track not found");

        }

    }

    public void removeTrackFromPlaylist(int trackId, int playlistId) {
        Optional<Track> foundTrack = trackRepository.findById(trackId);
        Optional<Playlist> foundPlaylist = playlistRepository.findById(playlistId);

        if (foundTrack.isPresent() && foundPlaylist.isPresent()) {
            if (!foundPlaylist.get().getTracks().contains(foundTrack.get())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have not added this track to this playlist yet");
            }
            foundPlaylist.get().getTracks().remove(foundTrack.get());
            playlistRepository.save(foundPlaylist.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Playlist or track not found");

        }

    }

    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public void deletePlaylistById(int playlistId) {
        playlistRepository.deleteById(playlistId);
    }

    public Playlist updatePlaylistById(int playlistId, Playlist playlist) {
        Optional<Playlist> foundPlaylist = playlistRepository.findById(playlistId);

        if (foundPlaylist.isPresent()) {
            Playlist updatedPlaylist = foundPlaylist.get();
            updatedPlaylist.setName(playlist.getName());
            updatedPlaylist.setDescription(playlist.getDescription());
//            updatedPlaylist.setCreator(playlist.getCreator());
//            updatedPlaylist.setTracks(playlist.getTracks());
            return playlistRepository.save(updatedPlaylist);

        } else {
            return null;
        }
    }

    public List<Playlist> getUsersPlaylists(User user) {
        return playlistRepository.findByCreator(user);
    }

    public List<Track> getPlaylistsTracks(Playlist playlist) {
        List<Track> list = trackRepository.findByInPlaylist(playlist);
        return list;
    }

    public Playlist getPlaylistById(int playlistId) {
        Optional<Playlist> foundPlaylist = playlistRepository.findById(playlistId);
        return foundPlaylist.orElse(null);
    }

    public Page<Playlist> findPaginatedPlaylists(Pageable pageable, List<Playlist> fullPlaylistList) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Playlist> pagedPlaylistList;

        try {
            if (fullPlaylistList.size() < startItem) {
                pagedPlaylistList = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, fullPlaylistList.size());
                pagedPlaylistList = fullPlaylistList.subList(startItem, toIndex);

                Page<Playlist> trackPage = new PageImpl<Playlist>(pagedPlaylistList, PageRequest.of(currentPage, pageSize), fullPlaylistList.size());

                return trackPage;
            }
        } catch (NullPointerException e) {
            pagedPlaylistList = Collections.emptyList();
            return new PageImpl<Playlist>(pagedPlaylistList, PageRequest.of(1, 1), 0);
        }

        return new PageImpl<Playlist>(pagedPlaylistList, PageRequest.of(1, 1), 0);

    }

}
