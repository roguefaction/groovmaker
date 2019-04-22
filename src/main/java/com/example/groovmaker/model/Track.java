package com.example.groovmaker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Table (name = "track")
public class Track implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "track_id")
    private int id;


    @Column(name = "artist")
    @Field
    @Length(min = 3, max = 50, message = "Artists must be between 3 to 50 characters")
    @NotEmpty(message = "Please enter the artist")
    private String artist;

    @Column(name = "title")
    @Field
    @Length(min = 3, max = 50, message = "Title must be between 3 to 50 characters")
    @NotEmpty(message = "Please enter the title")
    private String title;

    @Column(name = "description")
    @Field
    @Length(max = 500, message = "Description must be up to 500 characters long")
    private String description;

    @Column(name = "genre")
    @Field
    @NotEmpty(message = "Please select a genre")
    private String genre;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "uploader_id")
    @Field
    private int uploaderId;

    @Column(name = "last_modified")
    private ZonedDateTime lastModified;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User uploader;


    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "track_user",
            joinColumns = { @JoinColumn(name = "track_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> favoritedBy = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "tracks")
    private Set<Playlist> inPlaylist = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "ratingTrack")
    private Set<Rating> rating = new HashSet<>();

    public Track() {
    }

    public Track(@Length(min = 3, max = 50, message = "Artists must be between 3 to 50 characters") @NotEmpty(message = "Please enter the artist") String artist, @Length(min = 3, max = 50, message = "Title must be between 3 to 50 characters") @NotEmpty(message = "Please enter the title") String title, @Length(max = 500, message = "Description must be up to 500 characters long") String description, @NotEmpty(message = "Please select a genre") String genre, String imageUrl, String fileUrl, int uploaderId, User uploader) {
        this.artist = artist;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.fileUrl = fileUrl;
        this.uploaderId = uploaderId;
        this.uploader = uploader;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Set<Rating> getRating() {
        return rating;
    }

    public void setRating(Set<Rating> rating) {
        this.rating = rating;
    }

    public Set<User> getFavoritedBy() {
        return favoritedBy;
    }

    public void setFavoritedBy(Set<User> favoritedBy) {
        this.favoritedBy = favoritedBy;
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

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public Set<Playlist> getInPlaylist() {
        return inPlaylist;
    }

    public void setInPlaylist(Set<Playlist> inPlaylist) {
        this.inPlaylist = inPlaylist;
    }
}
