package com.example.groovmaker.service;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
