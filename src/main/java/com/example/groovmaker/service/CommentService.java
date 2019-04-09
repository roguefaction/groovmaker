package com.example.groovmaker.service;

import com.example.groovmaker.model.Comment;
import com.example.groovmaker.model.User;
import com.example.groovmaker.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {

        return commentRepository.save(comment);
    }

    public List<Comment> findAllComments() {
        return commentRepository.findAll();
    }

    public void deleteCommentById(int commentId) {

        Optional<Comment> foundComment = commentRepository.findById(commentId);

        if (foundComment.isPresent()) {
            commentRepository.delete(foundComment.get());
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");
        }

    }

    public List<Comment> getTracksComments(int trackId){
        return commentRepository.getAllByTrackId(trackId);

    }


    public Comment getCommentById(int commentId){
        Optional<Comment> foundComment = commentRepository.findById(commentId);

        if (foundComment.isPresent()) {
            return foundComment.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found");

        }

    }


}
