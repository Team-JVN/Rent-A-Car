package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.CommentClient;
import jvn.RentACar.client.RentRequestClient;
import jvn.RentACar.dto.both.CommentDTO;
import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.soap.comment.*;
import jvn.RentACar.dto.soap.comment.CommentDetails;
import jvn.RentACar.dto.soap.rentrequest.*;
import jvn.RentACar.enumeration.CommentStatus;
import jvn.RentACar.exceptionHandler.InvalidCommentDataException;
import jvn.RentACar.mapper.CommentDetailsMapper;
import jvn.RentACar.mapper.CommentDtoMapper;
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


    @Override
    public Comment createComment(Comment comment, Long id, Long rentInfoId) {

        User user = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByIdOrIdAndClientId(id, user.getId(), id, user.getId());
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
        User loggedInUser = userService.getLoginUser();

//        CheckIfCanCommentResponse response = commentClient.checkIfCanComment(rentRequest.getMainAppId(), rentInfo.getMainAppId());


//        if (response == null || !response.isValue()) {
//            System.out.println("null impl");
//
//            throw new InvalidCommentDataException("Cannot create comment.",
//                    HttpStatus.BAD_REQUEST);
//        }
        comment.setStatus(CommentStatus.APPROVED);
        comment.setSender(loggedInUser);

        rentInfo.getComments().add(comment);
        comment.setRentInfo(rentInfo);

//        CreateCommentResponse commentResponse = commentClient.createComment(rentRequest.getMainAppId(), rentInfo.getMainAppId(), comment);
//        System.out.println("commentResponse impl");
//        CommentDetails commentDetails = commentResponse.getCommentDetails();
//        if (commentDetails != null && commentDetails.getId() != null) {
//            System.out.println("if impl");
//            comment.setMainAppId(commentDetails.getId());
//        }
        rentRequestRepository.save(rentRequest);
//        commentRepository.save(comment);
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
            commentRepository.save(comment);
            rentInfo.getComments().add(comment);
            rentInfo.setRating(feedbackDTO.getRating());
            rentRequest.setRentInfos(new HashSet<>(rentInfos));
            rentRequestRepository.save(rentRequest);
//            LeaveFeedbackResponse leaveFeedbackResponse = commentClient.leaveFeedback(rentRequest.getMainAppId(), rentInfo.getMainAppId(), feedbackDTO);
//            System.out.println("leaveFeedbackResponse impl");
//            LeaveFeedbackDetails leaveFeedbackDetails = leaveFeedbackResponse.getLeaveFeedbackDetails();
//            if (leaveFeedbackDetails != null && leaveFeedbackDetails.getCommentDetails().get(0).getId() != null) {
//                System.out.println("if impl");
//                comment.setMainAppId(leaveFeedbackDetails.getCommentDetails().get(0).getId());
//            }
        }else{
            throw new InvalidCommentDataException("There is already comment for this rent info.", HttpStatus.BAD_REQUEST);
        }

        return feedbackDTO;
    }

    @Override
    public FeedbackDTO getFeedback(Long id, Long rentInfoId) {
        User user = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByIdOrIdAndClientId(id, user.getId(), id, user.getId());
        RentInfo rentInfo = rentInfoRepository.findByIdAndRentRequestId(rentInfoId, id);
//        synchronizeComments(rentRequest.getMainAppId(), rentInfo.getMainAppId());
        FeedbackDTO feedbackDTO = new FeedbackDTO();

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

//    @Scheduled(cron = "0 40 0/3 * * ?")
//    public void synchronizeComments(Long rentRequestId, Long rentInfoId) {
//        try {
//            GetAllCommentsDetailsResponse response = rentRequestClient.getAllCommentsDetails(rentRequestId, rentInfoId);
//            if (response == null) {
//                return;
//            }
//            List<CommentDetails> commentDetails = response.getCommentDetails();
//            if (commentDetails == null || commentDetails.isEmpty()) {
//                return;
//            }
//
//            for (CommentDetails current : commentDetails) {
//                Comment comment = commentDetailsMapper.toEntity(current);
//                Comment dbComment = commentRepository.findByMainAppId(comment.getMainAppId());
//                if (dbComment == null) {
//                    createSynchronize(comment);
//                } else {
////                    editSynchronize(rentRequest, dbRentRequest);
//                }
//            }
//            logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN",
//                    "[SOAP] Comments are successfully synchronized"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    private void createSynchronize(Comment comment) {
//        if (comment.getSender() == null) {
//            return;
//        }
//        Set<RentInfo> rentInfos = rentRequest.getRentInfos();
//        rentRequest.setRentInfos(null);
//        rentRequest = rentRequestRepository.saveAndFlush(rentRequest);
//        for (RentInfo rentInfo : rentInfos) {
//            if (rentInfo.getAdvertisement() == null) {
//                return;
//            }
//            rentInfo.setRentRequest(rentRequest);
//        }
//        rentRequest.setRentInfos(rentInfos);
//        rentRequestRepository.saveAndFlush(rentRequest);
    }

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, CommentDtoMapper commentDtoMapper,
                              RentRequestRepository rentRequestRepository, RentInfoRepository rentInfoRepository,
                              ClientRepository clientRepository, UserRepository userRepository,
                              UserService userService, CommentClient commentClient,
                              CommentDetailsMapper commentDetailsMapper, LogService logService) {
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
    }
}
