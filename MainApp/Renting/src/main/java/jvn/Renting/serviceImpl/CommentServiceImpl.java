package jvn.Renting.serviceImpl;

import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.exceptionHandler.InvalidCommentDataException;
import jvn.Renting.model.Comment;
import jvn.Renting.producer.CommentProducer;
import jvn.Renting.repository.CommentRepository;
import jvn.Renting.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private CommentProducer commentProducer;

    @Override
    public Comment get(Long id) {
        return commentRepository.findOneById(id);
    }

    @Override
    public List<Comment> getAll(String status) {
        List<Comment> comments;
        if (status.equals("all")) {
            comments = commentRepository.findAll();

        } else {
            comments = commentRepository.findAllByStatus(getCommentStatus(status));
        }

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        return comments;
    }

    @Override
    public Comment approve(Long id) {
        Comment comment = commentRepository.findOneById(id);
        if(comment.getStatus().equals(CommentStatus.APPROVED)){
            throw new InvalidCommentDataException("Comment is already approved.",
                    HttpStatus.NOT_FOUND);
        }
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Override
    public void reject(Long id, Long userId) {
        commentRepository.deleteById(id);
        //TODO:increase the number of rejected comments of User
        sendRejectedComment(userId);

    }

    private CommentStatus getCommentStatus(String status) {
        try {
            return CommentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidCommentDataException("Please choose some of existing comment's status.",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Async
    public void sendRejectedComment(Long clientId) {
        commentProducer.sendRejectedComment(clientId);
    }

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentProducer commentProducer) {
        this.commentRepository = commentRepository;
        this.commentProducer = commentProducer;
    }
}
