package jvn.RentACar.service;

import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.model.Comment;

import java.util.List;

public interface CommentService {

    Comment createComment(Comment comment, Long id, Long rentInfoId);

    FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId);

    FeedbackDTO getFeedback(Long id, Long rentInfoId);

    Comment get(Long id);

    List<Comment> getAll(String status);

    Comment approve(Long id);

    void reject(Long id, Long userID);

    void synchronize();
}
