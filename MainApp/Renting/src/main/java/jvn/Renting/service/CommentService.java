package jvn.Renting.service;

import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.model.Comment;

import java.util.Arrays;
import java.util.List;

public interface CommentService {
    Comment get(Long id);

    List<Comment> getAll(CommentStatus status);

    Comment approve(Long id);

    void reject(Long id, Long userID);
}
