package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.CommentClient;
import jvn.RentACar.client.RentRequestClient;
import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.both.UserDTO;
import jvn.RentACar.dto.soap.comment.*;
import jvn.RentACar.dto.soap.comment.CommentDetails;
import jvn.RentACar.dto.soap.rentrequest.*;
import jvn.RentACar.enumeration.CommentStatus;
import jvn.RentACar.exceptionHandler.InvalidCommentDataException;
import jvn.RentACar.mapper.CommentDetailsMapper;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.mapper.UserDtoMapper;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.*;
import jvn.RentACar.service.CommentService;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CommentServiceImpl implements CommentService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private CommentRepository commentRepository;

    private CommentDtoMapper commentDtoMapper;

    private RentRequestRepository rentRequestRepository;

    private RentInfoRepository rentInfoRepository;

    private ClientRepository clientRepository;

    private UserRepository userRepository;

    private UserService userService;

    private CommentClient commentClient;

    private CommentDetailsMapper commentDetailsMapper;

    private LogService logService;

    private UserDtoMapper userDtoMapper;


    @Override
    public Comment createComment(Comment comment, Long id, Long rentInfoId) {

        User user = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByIdOrIdAndClientId(id, user.getId(), id, user.getId());
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        User loggedInUser = userService.getLoginUser();

        CheckIfCanCommentResponse response = commentClient.checkIfCanComment(rentRequest.getMainAppId(), rentInfo.getMainAppId());

           if (response == null || !response.isValue()) {

            throw new InvalidCommentDataException("Cannot create comment.",
                    HttpStatus.BAD_REQUEST);
        }
        comment.setStatus(CommentStatus.APPROVED);
        comment.setSender(loggedInUser);

        rentInfo.getComments().add(comment);
        comment.setRentInfo(rentInfo);

        CreateCommentResponse commentResponse = commentClient.createComment(rentRequest.getMainAppId(), rentInfo.getMainAppId(), comment);

        CommentDetails commentDetails = commentResponse.getCommentDetails();
        if (commentDetails != null && commentDetails.getId() != null) {

            comment.setMainAppId(commentDetails.getId());
        }
//        rentRequestRepository.save(rentRequest);
        commentRepository.save(comment);
        return comment;
    }

    @Override
    public FeedbackDTO leaveFeedback(FeedbackDTO feedbackDTO, Long id, Long rentInfoId){
        User loggedInUser = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByIdOrIdAndClientId(id, loggedInUser.getId(), id, loggedInUser.getId());
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
        for(CommentDTO commentDTO: feedbackDTO.getComments()){
            commentDTO.setStatus(CommentStatus.APPROVED);
        }
//        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        if(commentRepository.findBySenderIdAndRentInfoId(loggedInUser.getId(), rentInfoId).isEmpty()){
            List<CommentDTO> commentDTOS = new ArrayList<>(feedbackDTO.getComments());
            Comment comment = new Comment();
            Client client = clientRepository.findById(loggedInUser.getId()).orElse(null);
            comment.setSender(client);
            comment.setStatus(CommentStatus.APPROVED);
            comment.setRentInfo(rentInfo);
            comment.setText(commentDTOS.get(0).getText());
            rentInfo.setRating(feedbackDTO.getRating());
            rentInfoRepository.save(rentInfo);
//            rentInfo.getComments().add(comment);
//            rentInfo.setRating(feedbackDTO.getRating());
//            rentRequest.setRentInfos(new HashSet<>(rentInfos));
//            rentRequestRepository.save(rentRequest);
            LeaveFeedbackResponse leaveFeedbackResponse = commentClient.leaveFeedback(rentRequest.getMainAppId(), rentInfo.getMainAppId(), feedbackDTO);

            LeaveFeedbackDetails leaveFeedbackDetails = leaveFeedbackResponse.getLeaveFeedbackDetails();
            if (leaveFeedbackDetails != null && leaveFeedbackDetails.getCommentDetails().get(0).getId() != null) {

                comment.setMainAppId(leaveFeedbackDetails.getCommentDetails().get(0).getId());
            }
            commentRepository.save(comment);
        }else{
            throw new InvalidCommentDataException("There is already comment for this rent info.", HttpStatus.BAD_REQUEST);
        }

        return feedbackDTO;
    }

    @Override
    public FeedbackDTO getFeedback(Long id, Long rentInfoId) {
        synchronizeComments();
        User user = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByIdOrIdAndClientId(id, user.getId(), id, user.getId());
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
//        synchronizeComments(rentRequest.getMainAppId(), rentInfo.getMainAppId());
        FeedbackDTO feedbackDTO = new FeedbackDTO();

        feedbackDTO.setRating(rentInfo.getRating());
        feedbackDTO.setComments(new HashSet<>());
        for(Comment comment: rentInfo.getComments()){
            if(comment.getStatus().equals(CommentStatus.APPROVED)){
                UserDTO sender = userDtoMapper.toDto(comment.getSender());
                CommentDTO commentDTO = commentDtoMapper.toDto(comment);
                commentDTO.setSender(sender);
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
        synchronizeComments();
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

    @Override
    public void synchronize() {
        synchronizeComments();
    }

    private CommentStatus getCommentStatus(String status) {
        try {
            return CommentStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new InvalidCommentDataException("Please choose some of existing comment's status.",
                    HttpStatus.NOT_FOUND);
        }
    }

    @Scheduled(cron = "0 40 0/3 * * ?")
    public void synchronizeComments() {
        try {
            List<RentInfo> rentInfos = rentInfoRepository.findAll();
            for(RentInfo rentInfo: rentInfos){
                GetAllCommentsDetailsResponse response = commentClient.getAllCommentsDetails(rentInfo.getRentRequest().getMainAppId(), rentInfo.getMainAppId());
                if (response == null) {
                    continue;
                }
                List<CommentDetails> commentDetails = response.getCommentDetails();
                if (commentDetails == null || commentDetails.isEmpty()) {
                    continue;
                }

                for (CommentDetails current : commentDetails) {
                    Comment comment = commentDetailsMapper.toEntity(current);
                    Comment dbComment = commentRepository.findByMainAppId(comment.getMainAppId());
                    if (dbComment == null) {
                        createSynchronize(comment, rentInfo);
                    } else {
                        editSynchronize(comment, dbComment, rentInfo);
                    }
                }
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN",
                        "[SOAP] Comments are successfully synchronized"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void createSynchronize(Comment comment, RentInfo rentInfo) {
        if (comment.getSender() == null) {
            return;
        }
        comment.setRentInfo(rentInfo);
        commentRepository.saveAndFlush(comment);
    }
    private void editSynchronize(Comment comment, Comment dbComment, RentInfo rentInfo) {
        if (comment.getSender() == null) {
            return;
        }
        dbComment.setStatus(comment.getStatus());
        dbComment.setText(comment.getText());
        dbComment.setSender(comment.getSender());
        dbComment.setRentInfo(rentInfo);

        commentRepository.save(dbComment);
    }

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper,
                              RentRequestRepository rentRequestRepository, RentInfoRepository rentInfoRepository,
                              ClientRepository clientRepository, UserRepository userRepository,
                              UserService userService, CommentClient commentClient,
                              CommentDetailsMapper commentDetailsMapper, LogService logService, UserDtoMapper userDtoMapper) {
        this.commentRepository = commentRepository;
        this.commentDtoMapper = commentDtoMapper;
        this.rentRequestRepository = rentRequestRepository;
        this.rentInfoRepository = rentInfoRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.commentDetailsMapper = commentDetailsMapper;
        this.logService = logService;
        this.commentClient = commentClient;
        this.userDtoMapper = userDtoMapper;
    }
}
