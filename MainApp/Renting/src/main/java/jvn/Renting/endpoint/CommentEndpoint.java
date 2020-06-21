package jvn.Renting.endpoint;

import jvn.Renting.client.UserClient;
import jvn.Renting.dto.both.CommentDTO;
import jvn.Renting.dto.both.FeedbackDTO;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.comment.*;
import jvn.Renting.dto.soap.rentrequest.*;
import jvn.Renting.dto.soap.comment.CommentDetails;
import jvn.Renting.mapper.CommentDetailsMapper;
import jvn.Renting.mapper.CommentDtoMapper;
import jvn.Renting.model.Comment;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.service.CommentService;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class CommentEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String NAMESPACE_URI = "http://www.soap.dto/comment";
    private CommentDetailsMapper commentDetailsMapper;

    private CommentDtoMapper commentDtoMapper;

    private UserClient userClient;

    private LogProducer logProducer;

    private CommentService commentService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createCommentRequest")
    @ResponsePayload
    public CreateCommentResponse createComment(@RequestPayload CreateCommentRequest request) {

        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        CommentDetails commentDetails = request.getCommentDetails();
        commentDetails.setId(null);
        try {
            Comment comment = commentService.createComment(commentDetailsMapper.toEntity(commentDetails),
                    request.getRentRequestId(), request.getRentInfoId(), dto.getId());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ", String.format("[SOAP] User %s successfully created comment %s", dto.getId(), comment.getId())));
            commentDetails = commentDetailsMapper.toDto(comment);
        } catch (DateTimeParseException e) {
            return null;
        }

        CreateCommentResponse response = new CreateCommentResponse();
        response.setCommentDetails(commentDetails);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "leaveFeedbackRequest")
    @ResponsePayload
    public LeaveFeedbackResponse leaveFeedback(@RequestPayload LeaveFeedbackRequest request) {

        UserInfoDTO dto = userClient.getUser(request.getEmail());
        LeaveFeedbackDetails newFeedback = new LeaveFeedbackDetails();
        if (dto == null) {
            return null;
        }
        LeaveFeedbackDetails leaveFeedbackDetails = request.getLeaveFeedbackDetails();
        for(CommentDetails commentDetails: leaveFeedbackDetails.getCommentDetails()){
            commentDetails.setId(null);
        }

        try {
            FeedbackDTO fdto = new FeedbackDTO();
            fdto.setRating(leaveFeedbackDetails.getRating());

            List<Comment> list = leaveFeedbackDetails.getCommentDetails().stream().map(commentDetailsMapper::toEntity).
                    collect(Collectors.toList());

            List<CommentDTO> listDTO = list.stream().map(commentDtoMapper::toDto).
                    collect(Collectors.toList());
            fdto.setComments(new HashSet<>(listDTO));
            FeedbackDTO feedbackDTO = commentService.leaveFeedback(fdto,
                    request.getRentRequestId(), request.getRentInfoId(), dto.getId(),
                    leaveFeedbackDetails.getCommentDetails().get(0).getUserName());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ",
                    String.format("[SOAP] User %s successfully created comment %s", dto.getId(), leaveFeedbackDetails.getCommentDetails().get(0).getId())));

            newFeedback.setRating(feedbackDTO.getRating());
            List<Comment> comments = feedbackDTO.getComments().stream().map(commentDtoMapper::toEntity).
                    collect(Collectors.toList());
            List<CommentDetails> commentDetails = comments.stream().map(commentDetailsMapper::toDto).
                    collect(Collectors.toList());
            newFeedback.getCommentDetails().addAll(commentDetails);
        } catch (DateTimeParseException e) {
            return null;
        }

        LeaveFeedbackResponse response = new LeaveFeedbackResponse();
        response.setLeaveFeedbackDetails(newFeedback);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllCommentsRequest")
    @ResponsePayload
    public GetAllCommentsDetailsResponse getAllComments(@RequestPayload GetAllCommentsDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        FeedbackDTO feedbackDTO = commentService.getFeedback(request.getRentRequestId(), request.getRentInfoId(), dto.getId());

        List<CommentDTO> commentDTOS = new ArrayList<>(feedbackDTO.getComments());

        List<Comment> comments = commentDTOS.stream().map(commentDtoMapper::toEntity).collect(Collectors.toList());;
        List<CommentDetails> list = comments.stream().map(commentDetailsMapper::toDto).collect(Collectors.toList());
        GetAllCommentsDetailsResponse response = new GetAllCommentsDetailsResponse();
        response.getCommentDetails().addAll(list);
        return response;
    }
    @Autowired
    public CommentEndpoint(CommentDetailsMapper commentDetailsMapper, CommentDtoMapper commentDtoMapper, UserClient userClient, LogProducer logProducer, CommentService commentService) {
        this.commentDetailsMapper = commentDetailsMapper;
        this.commentDtoMapper = commentDtoMapper;
        this.userClient = userClient;
        this.logProducer = logProducer;
        this.commentService = commentService;
    }
}
