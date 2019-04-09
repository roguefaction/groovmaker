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
import java.util.HashSet;
import java.util.Set;

@Entity
@Indexed
@Table (name = "playlist")
public class Playlist implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "playlist_id")
    private int id;

    @Column(name = "name")
    @Field
    @Length(min = 3, max = 50, message = "Name must be between 3 to 50 characters")
    @NotEmpty(message = "Please enter the name")
    private String name;

    @Column(name = "description")
    @Field
    @Length(max = 500, message = "Description must be up to 500 characters long")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User creator;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "playlist_track",
            joinColumns = { @JoinColumn(name = "playlist_id") },
            inverseJoinColumns = { @JoinColumn(name = "track_id") })
    private Set<Track> tracks = new HashSet<>();



    public Playlist() {
    }

    public Playlist(@Length(min = 3, max = 50, message = "Name must be between 3 to 50 characters") @NotEmpty(message = "Please enter the name") String name, @Length(max = 500, message = "Description must be up to 500 characters long") String description, User creator, Set<Track> tracks) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.tracks = tracks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<Track> getTracks() {
        return tracks;
    }

    public void setTracks(Set<Track> tracks) {
        this.tracks = tracks;
    }
}
