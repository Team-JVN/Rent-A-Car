package jvn.RentACar.serviceImpl;

import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.enumeration.CommentStatus;
import jvn.RentACar.exceptionHandler.InvalidCommentDataException;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.*;
import jvn.RentACar.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    private CommentDtoMapper commentDtoMapper;

    private RentRequestRepository rentRequestRepository;

    private RentInfoRepository rentInfoRepository;

    private ClientRepository clientRepository;

    private UserRepository userRepository;

    @Override
    public Comment createComment(Comment comment, Long id, Long rentInfoId, Long userId) {
        //TODO: which status has the comment made by agent
        comment.setStatus(CommentStatus.APPROVED);
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        rentInfo.getComments().add(comment);
        comment.setRentInfo(rentInfo);
        rentRequestRepository.save(rentRequest);


//        commentRepository.save(comment);
        return comment;
    }

    @Override
    public FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId, Long userId, String userName){

        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
//        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        if(commentRepository.findBySenderIdAndRentInfoId(userId, rentInfoId).isEmpty()){
            RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
            List<CommentDTO> commentDTOS = new ArrayList<>(feedbackDTO.getComments());
            Comment comment = new Comment();
            Client client = clientRepository.findById(userId).orElse(null);
            comment.setSender(client);
            comment.setStatus(CommentStatus.AWAITING);
            comment.setRentInfo(rentInfo);
            comment.setText(commentDTOS.get(0).getText());
            commentRepository.save(comment);
            rentInfo.getComments().add(comment);
            rentInfo.setRating(feedbackDTO.getRating());
            rentRequest.setRentInfos(new HashSet<>(rentInfos));
            rentRequestRepository.save(rentRequest);

        }else{
            throw new InvalidCommentDataException("There is already comment for this rent info.", HttpStatus.BAD_REQUEST);
        }

        return feedbackDTO;
    }

    @Override
    public FeedbackDTO getFeedback(Long id, Long rentInfoId, Long userId) {
        FeedbackDTO feedbackDTO = new FeedbackDTO();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        feedbackDTO.setRating(rentInfo.getRating());
        feedbackDTO.setComments(new HashSet<>());
        for(Comment comment: rentInfo.getComments()){
            if(comment.getStatus().equals(CommentStatus.APPROVED)){

                CommentDTO commentDTO = commentDtoMapper.toDto(comment);
                feedbackDTO.getComments().add(commentDTO);

            }

        }



        return feedbackDTO;
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

    }

    private CommentStatus getCommentStatus(String status) {
        try {
            return CommentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidCommentDataException("Please choose some of existing comment's status.",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper, RentRequestRepository rentRequestRepository, RentInfoRepository rentInfoRepository, ClientRepository clientRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentDtoMapper = commentDtoMapper;
        this.rentRequestRepository = rentRequestRepository;
        this.rentInfoRepository = rentInfoRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }
}
