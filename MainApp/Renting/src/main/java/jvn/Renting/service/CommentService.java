package jvn.Renting.service;

import jvn.Renting.dto.both.FeedbackDTO;
import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.model.Comment;

import java.util.Arrays;
import java.util.List;

public interface CommentService {
    Comment get(Long id);

    List<Comment> getAll(String status);

    Comment approve(Long id);

    void reject(Long id, Long userID);

    Comment createComment(Comment comment, Long id, Long rentInfoId, Long userId);

    FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId, Long userId, String userName, Boolean canCreateComments);

    FeedbackDTO getFeedback(Long id, Long rentInfoId, Long userId);

}
