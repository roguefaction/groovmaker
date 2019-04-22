package com.example.groovmaker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private int id;

    @Column(name = "email")
    @Email(message = "Please enter a valid email address")
    @NotEmpty(message = "Please enter an email address")
    private String email;

    @Column(name = "name")
    @NotEmpty(message = "Please enter your name")
    private String name;

    @Column(name = "password")
    @NotEmpty(message = "Please enter a valid password")
    @Length(min = 5, message = "Your password must be atleast 5 characters long")
    private String password;

    @Column(name = "active")  // recommended by Spring for active user numbers
    private int active;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;


    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "favoritedBy")
    private Set<Track> favoriteTracks = new HashSet<>();

    @JsonBackReference
    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "ratingUser")
    private Set<Rating> trackRatings = new HashSet<>();

//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.MERGE
//            })
//    @JoinTable(name = "followed_user",
//            joinColumns = {@JoinColumn(name = "user_id")},
//            inverseJoinColumns = {@JoinColumn(name = "followed_user_id")})
//    private Set<User> followedUsers = new HashSet<>();
//
//    @JsonBackReference
//    @ManyToMany(fetch = FetchType.LAZY,
//            cascade = {
//                    CascadeType.PERSIST,
//                    CascadeType.MERGE
//            },
//            mappedBy = "followedUsers")
//    private Set<Rating> followingUsers = new HashSet<>();

    @ManyToMany(cascade={CascadeType.ALL})
    @JoinTable(name="user_follower",
            joinColumns={@JoinColumn(name="user_id")},
            inverseJoinColumns={@JoinColumn(name="follower_id")})
    private Set<User> followers = new HashSet<>();

    @JsonBackReference
    @ManyToMany(mappedBy="followers")
    private Set<User> following = new HashSet<>();

    public User(@Email(message = "Please enter a valid email address") @NotEmpty(message = "Please enter an email address") String email, @NotEmpty(message = "Please enter your name") String name, @NotEmpty(message = "Please enter a valid password") @Length(min = 5, message = "Your password must be atleast 5 characters long") String password, Set<Role> roles) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.roles = roles;
    }

    public User() {
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    //    public Set<User> getFollowedUsers() {
//        return followedUsers;
//    }
//
//    public void setFollowedUsers(Set<User> followedUsers) {
//        this.followedUsers = followedUsers;
//    }
//
//    public Set<Rating> getFollowingUsers() {
//        return followingUsers;
//    }
//
//    public void setFollowingUsers(Set<Rating> followingUsers) {
//        this.followingUsers = followingUsers;
//    }

    public Set<Track> getFavoriteTracks() {
        return favoriteTracks;
    }

    public Set<Rating> getTrackRatings() {
        return trackRatings;
    }

    public void setTrackRatings(Set<Rating> trackRatings) {
        this.trackRatings = trackRatings;
    }

    public void setFavoriteTracks(Set<Track> favoriteTracks) {
        this.favoriteTracks = favoriteTracks;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
