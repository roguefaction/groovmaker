package com.example.groovmaker.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity
@Table (name = "track")
public class Track implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "track_id")
    private int id;

    @Column(name = "artist")
    @Length(min = 3, max = 50, message = "Artists must be between 3 to 50 characters")
    @NotEmpty(message = "Please enter the artist")
    private String artist;

    @Column(name = "title")
    @Length(min = 3, max = 50, message = "Title must be between 3 to 50 characters")
    @NotEmpty(message = "Please enter the title")
    private String title;

    @Column(name = "description")
    @Length(max = 500, message = "Description must be up to 500 characters long")
    private String description;

    @Column(name = "genre")
    @NotEmpty(message = "Please select a genre")
    private String genre;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "file_url")
    private String fileUrl;

    @JoinTable(name = "user", joinColumns = @JoinColumn(name = "uploader_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "uploader_id")
    private int uploaderId;

    public Track(@NotEmpty(message = "Please enter the artist name") String artist, @NotEmpty(message = "Please enter the title") String title, @NotEmpty(message = "Please enter the description") String description, String genre, String imageUrl, String fileUrl, int uploaderId) {
        this.artist = artist;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.uploaderId = uploaderId;
    }

    public Track() {
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
