package com.example.groovmaker.repository;

import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {

    List<Track> findByUploader(User uploader);
    List<Track> findByUploaderId(int uploaderId);

}
