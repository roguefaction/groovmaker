package com.example.groovmaker.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table (name = "comment")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "content")
    @NotEmpty(message = "comment must not be empty")
    @Length(min = 3, max = 100, message = "comment bus be between 3 and 100 symbols")
    private String content;

    @Column(name = "timestamp")
    private ZonedDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "track", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Track track;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User commenter;

    public Comment(@NotEmpty(message = "comment must not be empty") @Length(min = 3, max = 100, message = "comment bus be between 3 and 100 symbols") String content, ZonedDateTime timestamp, Track track, User commenter) {
        this.content = content;
        this.timestamp = timestamp;
        this.track = track;
        this.commenter = commenter;
    }

    public Comment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public User getCommenter() {
        return commenter;
    }

    public void setCommenter(User commenter) {
        this.commenter = commenter;
    }
}
