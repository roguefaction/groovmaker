package com.example.groovmaker.controller;

import com.example.groovmaker.model.Comment;
import com.example.groovmaker.model.Track;
import com.example.groovmaker.model.User;
import com.example.groovmaker.service.CommentService;
import com.example.groovmaker.service.TrackService;
import com.example.groovmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Controller
public class CommentController {

    private TrackService trackService;
    private CommentService commentService;
    private UserService userService;

    @Autowired
    public CommentController(TrackService trackService, CommentService commentService, UserService userService) {
        this.trackService = trackService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/track/{id}/comment")
    public String postComment(@PathVariable("id") int trackId, @Valid Comment comment, BindingResult bindingResult){

        User commenter = getAuth();

        Track currentTrack = trackService.getTrackById(trackId);
        comment.setTrack(currentTrack);
        comment.setCommenter(commenter);

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dateAndTimeInLA = ZonedDateTime.ofInstant(now, zoneId);

        comment.setTimestamp(dateAndTimeInLA);

        commentService.createComment(comment);

        return "redirect:/track/" + trackId;
    }

    @GetMapping("/track/{track_id}/comment/{comment_id}/delete")
    public String deleteComment(@PathVariable("track_id") int trackId, @PathVariable("comment_id") int commentId){

        User user = getAuth();
        Comment comment = commentService.getCommentById(commentId);

        if(user.getId() == comment.getCommenter().getId()){
            commentService.deleteCommentById(comment.getId());
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only commenting user can delete this comment");
        }

        return "redirect:/track/" + trackId;
    }


    private User getAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByEmail(authentication.getName());
    }

}
