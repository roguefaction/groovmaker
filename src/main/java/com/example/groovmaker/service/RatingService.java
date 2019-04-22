package com.example.groovmaker.service;

import com.example.groovmaker.model.Rating;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.RatingRepository;
import com.example.groovmaker.repository.TrackRepository;
import com.example.groovmaker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {


    private TrackRepository trackRepository;
    private UserRepository userRepository;
    private RatingRepository ratingRepository;

    @Autowired
    public RatingService(TrackRepository trackRepository, UserRepository userRepository, RatingRepository ratingRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
    }


    public void addRating(Rating rating, User user, int trackId) {

        Optional<Track> foundTrack = trackRepository.findById(trackId);
        if (foundTrack.isPresent()) {
            foundTrack.get().getRating().add(rating);
            user.getTrackRatings().add(rating);
            rating.getRatingUser().add(user);
            rating.getRatingTrack().add(foundTrack.get());
            ratingRepository.save(rating);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No track found");
        }
    }

    public Float getTracksAverageRating(int trackId) {

        Optional<Track> foundTrack = trackRepository.findById(trackId);
        if (foundTrack.isPresent()) {
            return calculateAverageRating(ratingRepository.getAllByRatingTrack(foundTrack.get()));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No track found");
        }

    }

    public List<Rating> getTracksRating(int trackId) {

        Optional<Track> foundTrack = trackRepository.findById(trackId);
        if (foundTrack.isPresent()) {
            return ratingRepository.getAllByRatingTrack(foundTrack.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No track found");
        }

    }

    public List<Rating> getUsersRating(User user) {
        return ratingRepository.getAllByRatingUser(user);
    }

    private Float calculateAverageRating(List<Rating> ratings) {

        float sum = 0;
        int count = 0;
        if (!ratings.isEmpty()) {
            for (Rating rating : ratings) {
                sum += rating.getRating();
                count++;
            }
            return sum / count;
        }
        return 0f;
    }
}
