package com.example.groovmaker.repository;

import com.example.groovmaker.model.Rating;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> getAllByRatingTrack(Track track);
    List<Rating> getAllByRatingUser(User user);


}
