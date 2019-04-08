package com.example.groovmaker.service;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class TrackService {

    private TrackRepository trackRepository;

    @Autowired
    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<Track> getAllTracks() {
        return trackRepository.findAll();
    }

    public Page<Track> findPaginatedTracks(Pageable pageable, List<Track> fullTrackList) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Track> pagedTrackList;

        try {
            if (fullTrackList.size() < startItem) {
                pagedTrackList = Collections.emptyList();
            } else {
                int toIndex = Math.min(startItem + pageSize, fullTrackList.size());
                pagedTrackList = fullTrackList.subList(startItem, toIndex);

                Page<Track> trackPage = new PageImpl<Track>(pagedTrackList, PageRequest.of(currentPage, pageSize), fullTrackList.size());

                return trackPage;
            }
        } catch (NullPointerException e) {
            pagedTrackList = Collections.emptyList();
            return new PageImpl<Track>(pagedTrackList, PageRequest.of(1, 1), 0);
        }

        return new PageImpl<Track>(pagedTrackList, PageRequest.of(1, 1), 0);

    }

    public Track getTrackById(int trackId) {
        Optional<Track> foundTrack = trackRepository.findById(trackId);
        return foundTrack.orElse(null);

    }

    public Track createTrack(Track track) {
        return trackRepository.save(track);
    }

    public void deleteTrackById(int trackId) {
        trackRepository.deleteById(trackId);
    }

    public Track updateTrackById(int trackId, Track track) {
        Optional<Track> foundTrack = trackRepository.findById(trackId);

        if (foundTrack.isPresent()) {
            Track updatedTrack = foundTrack.get();
            updatedTrack.setTitle(track.getTitle());
            updatedTrack.setArtist(track.getArtist());
            updatedTrack.setDescription(track.getDescription());
            updatedTrack.setGenre(track.getGenre());
            updatedTrack.setImageUrl(track.getImageUrl());
            if (track.getFileUrl() != null)
                updatedTrack.setFileUrl(track.getFileUrl());
            updatedTrack.setUploader(foundTrack.get().getUploader());

            return trackRepository.save(updatedTrack);

        } else {
            return null;
        }


    }

    public List<Track> findByUploader(User uploader) {
        return this.trackRepository.findByUploader(uploader);
    }

    public List<Track> findByUploaderId(int uploaderId) {
        return this.trackRepository.findByUploaderId(uploaderId);
    }


    public List<Track> getTracksByGenre(String genre) {
        List<Track> allTracks = trackRepository.findAll();

        Iterator<Track> trackIterator = allTracks.iterator();

        while (trackIterator.hasNext()) {
            Track currentTrack = trackIterator.next();

            if (!currentTrack.getGenre().equalsIgnoreCase(genre)) {
                trackIterator.remove();
            }
        }

        return allTracks;

    }

    public void addToFavorites(int id, User user) {

        Optional<Track> foundTrack = trackRepository.findById(id);

        if (foundTrack.isPresent()) {

            if (foundTrack.get().getFavoritedBy().contains(user)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are have already favored this track");
            } else {
                user.getFavoriteTracks().add(foundTrack.get());
                foundTrack.get().getFavoritedBy().add(user);
                trackRepository.save(foundTrack.get());
            }
        }

    }

    public void removeFromFavorites(int id, User user) {

        Optional<Track> foundTrack = trackRepository.findById(id);

        if (foundTrack.isPresent()) {

            if (foundTrack.get().getFavoritedBy().contains(user)) {
                foundTrack.get().getFavoritedBy().remove(user);
                trackRepository.save(foundTrack.get());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are have not favored this track");
            }
        }

    }

    public List<Track> getAllFavorites(User user){
        return trackRepository.findByFavoritedBy(user);
    }


}
