package com.example.groovmaker.model;

import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Table (name = "rating")
public class Rating implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rating_id")
    private int id;


    @Column(name = "rating")
    @NotNull(message = "Rating must be present")
    private int rating;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "rating_user",
            joinColumns = { @JoinColumn(name = "rating_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") })
    private Set<User> ratingUser = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "rating_track",
            joinColumns = { @JoinColumn(name = "rating_id") },
            inverseJoinColumns = { @JoinColumn(name = "track_id") })
    private Set<Track> ratingTrack = new HashSet<>();

    public Rating() {
    }

    public Rating(@Length(max = 1, message = "Rating must be a single number") @NotEmpty(message = "Rating must be present") int rating, Set<User> ratingUser, Set<Track> ratingTrack) {
        this.rating = rating;
        this.ratingUser = ratingUser;
        this.ratingTrack = ratingTrack;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Set<User> getRatingUser() {
        return ratingUser;
    }

    public void setRatingUser(Set<User> ratingUser) {
        this.ratingUser = ratingUser;
    }

    public Set<Track> getRatingTrack() {
        return ratingTrack;
    }

    public void setRatingTrack(Set<Track> ratingTrack) {
        this.ratingTrack = ratingTrack;
    }
}
