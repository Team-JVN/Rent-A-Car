package jvn.Renting.serviceImpl;

import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.FeedbackDTO;
import jvn.Renting.enumeration.CommentStatus;
import jvn.Renting.exceptionHandler.InvalidCommentDataException;
import jvn.Renting.mapper.CommentDtoMapper;
import jvn.Renting.model.Comment;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.CommentProducer;
import jvn.Renting.repository.CommentRepository;
import jvn.Renting.repository.RentInfoRepository;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private CommentProducer commentProducer;

    private RentRequestRepository rentRequestRepository;

    private CommentDtoMapper commentDtoMapper;

    private RentInfoRepository rentInfoRepository;

    @Override
    public Comment createComment(Comment comment, Long id, Long rentInfoId, Long userId) {
        //TODO: which status has the comment made by agent
        comment.setStatus(CommentStatus.APPROVED);
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        checkIfCanComment(rentInfo);
        rentInfo.getComments().add(comment);
        comment.setRentInfo(rentInfo);
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId, Long userId, String userName, Boolean canCreateComments) {

        if (!canCreateComments) {
            throw new InvalidCommentDataException("You are not allowed to create comments. ", HttpStatus.BAD_REQUEST);
        }
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
//        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        if (commentRepository.findBySenderIdAndRentInfoId(userId, rentInfoId).isEmpty()) {
            RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
            List<CommentDTO> commentDTOS = new ArrayList<>(feedbackDTO.getComments());
            Comment comment = new Comment();
            comment.setSenderId(userId);
            comment.setSenderName(userName);
            comment.setStatus(CommentStatus.AWAITING);
            comment.setRentInfo(rentInfo);
            comment.setText(commentDTOS.get(0).getText());
            comment = commentRepository.save(comment);
            commentDTOS.set(0, commentDtoMapper.toDto(comment));
            feedbackDTO.getComments().clear();
            feedbackDTO.getComments().addAll(commentDTOS);
//            rentInfo.getComments().add(comment);
            rentInfo.setRating(feedbackDTO.getRating());
            commentProducer.sendUpdateCarRating(rentInfo.getCar(), feedbackDTO.getRating());
            rentInfoRepository.save(rentInfo);
//            sendUpdateCarAvgRating(rentInfo.getId(), rentInfo.getRating());
//            rentRequest.setRentInfos(new HashSet<>(rentInfos));
//            rentRequestRepository.save(rentRequest);

        } else {
            throw new InvalidCommentDataException("There is already comment for this rent info.", HttpStatus.BAD_REQUEST);
        }

        return feedbackDTO;
    }

    @Override
    public FeedbackDTO getFeedback(Long id, Long rentInfoId, Long userId) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        feedbackDTO.setRating(rentInfo.getRating());
        feedbackDTO.setComments(new HashSet<>());
        for (Comment comment : rentInfo.getComments()) {
            if (comment.getStatus().equals(CommentStatus.APPROVED)) {
                CommentDTO commentDTO = commentDtoMapper.toDto(comment);
                feedbackDTO.getComments().add(commentDTO);
            }

        }


        return feedbackDTO;
    }

    @Override
    public void checkIfCanComment(RentInfo rentInfo) {
        List<Comment> comments = commentRepository.findByRentInfoId(rentInfo.getId());
        if (comments != null) {
            if (comments.size() > 1) {
                throw new InvalidCommentDataException("Cannon crete comment.",
                        HttpStatus.BAD_REQUEST);
            } else {
                for (Comment comment : comments) {
                    if (comment.getStatus().equals("AWAITING")) {
                        throw new InvalidCommentDataException("Cannon crete comment.",
                                HttpStatus.BAD_REQUEST);
                    }
                }
            }
        } else {
            throw new InvalidCommentDataException("Cannon crete comment.",
                    HttpStatus.BAD_REQUEST);
        }
    }

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
        if (comment.getStatus().equals(CommentStatus.APPROVED)) {
            throw new InvalidCommentDataException("Comment is already approved.",
                    HttpStatus.NOT_FOUND);
        }
        comment.setStatus(CommentStatus.APPROVED);
        return commentRepository.save(comment);
    }

    @Override
    public void reject(Long id, Long userId) {
        Comment comment = commentRepository.findOneById(id);
        comment.setRentInfo(null);
        commentRepository.deleteById(id);
        //TODO:increase the number of rejected comments of User
        sendRejectedComment(comment.getSenderId());

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
    public void sendRejectedComment(Long userId) {
        commentProducer.sendRejectedComment(userId);
    }

//    @Async
//    public void sendUpdateCarAvgRating(Long rentInfoId, Integer rating){
//        commentProducer.sendRejectedComment(userId);
//    }

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentProducer commentProducer,
                              CommentDtoMapper commentDtoMapper, RentRequestRepository rentRequestRepository,
                              RentInfoRepository rentInfoRepository) {
        this.commentRepository = commentRepository;
        this.commentProducer = commentProducer;
        this.commentDtoMapper = commentDtoMapper;
        this.rentRequestRepository = rentRequestRepository;
        this.rentInfoRepository = rentInfoRepository;
    }
}
