package jvn.Renting.serviceImpl;

import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.exceptionHandler.InvalidCommentDataException;
import jvn.Renting.model.Comment;
import jvn.Renting.repository.CommentRepository;
import jvn.Renting.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    @Override
    public Comment get(Long id) {
        return commentRepository.findOneById(id);
    }

    @Override
    public List<Comment> getAll(CommentStatus status) {
        return commentRepository.findAllByStatus(status);
    }

    @Override
    public Comment approve(Long id) {
        Comment comment = commentRepository.findOneById(id);
        if(!comment.getStatus().equals(CommentStatus.APPROVED)){
            throw new InvalidCommentDataException("Comment is already approved.",
                    HttpStatus.NOT_FOUND);
        }
        comment.setStatus(CommentStatus.APPROVED);
        return comment;
    }

    @Override
    public void reject(Long id, Long userId) {
        commentRepository.deleteById(id);
        //TODO:increase the number of rejected comments of User

    }
}
