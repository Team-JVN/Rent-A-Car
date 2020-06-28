package jvn.RentACar.client;

import jvn.RentACar.dto.both.FeedbackDTO;
import jvn.RentACar.dto.soap.comment.*;
import jvn.RentACar.dto.soap.comment.CommentDetails;
import jvn.RentACar.mapper.CommentDetailsMapper;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.mapper.MessageDetailsMapper;
import jvn.RentACar.mapper.RentRequestDetailsMapper;
import jvn.RentACar.model.Comment;
import jvn.RentACar.model.User;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import java.util.List;
import java.util.stream.Collectors;

public class CommentClient  extends WebServiceGatewaySupport {

    @Autowired
    private UserService userService;


    @Autowired
    private CommentDetailsMapper commentDetailsMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private RentInfoService rentInfoService;


    public CheckIfCanCommentResponse checkIfCanComment(Long rentRequestId, Long rentInfoId){

        CheckIfCanCommentRequest request = new CheckIfCanCommentRequest();
        request.setRentRequestId(rentRequestId);
        request.setRentInfoId(rentInfoId);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CheckIfCanCommentResponse response = (CheckIfCanCommentResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);

        return response;
    }

    public CreateCommentResponse createComment(Long rentRequestId, Long rentInfoId, Comment comment){
        comment.setRentInfo(rentInfoService.get(rentInfoId));
        CommentDetails commentDetails = commentDetailsMapper.toDto(comment);
        CreateCommentRequest request = new CreateCommentRequest();
        request.setRentRequestId(rentRequestId);
        request.setRentInfoId(rentInfoId);
        request.setCommentDetails(commentDetails);
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        CreateCommentResponse response = (CreateCommentResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;

    }

    public GetAllCommentsDetailsResponse getAllCommentsDetails(Long rentRequestId, Long rentInfoId){

        GetAllCommentsDetailsRequest request = new GetAllCommentsDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setRentRequestId(rentRequestId);
        request.setRentInfoId(rentInfoId);
        request.setEmail(user.getEmail());
        GetAllCommentsDetailsResponse response = (GetAllCommentsDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public LeaveFeedbackResponse leaveFeedback(Long rentRequestId, Long rentInfoId, FeedbackDTO feedbackDTO){
        LeaveFeedbackRequest request = new LeaveFeedbackRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        request.setRentRequestId(rentRequestId);
        request.setRentInfoId(rentInfoId);
        LeaveFeedbackDetails feedbackDetails = new LeaveFeedbackDetails();
        feedbackDetails.setRating(feedbackDTO.getRating());
        List<Comment> comments = feedbackDTO.getComments().stream().map(commentDtoMapper::toEntity).collect(Collectors.toList());
        feedbackDetails.getCommentDetails().addAll(comments.stream().map(commentDetailsMapper::toDto).collect(Collectors.toList()));
        request.setLeaveFeedbackDetails(feedbackDetails);
        LeaveFeedbackResponse response = (LeaveFeedbackResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetFeedbackDetailsResponse getFeedbackDetails (Long rentRequestId, Long rentInfoId){
        GetFeedbackDetailsRequest request = new GetFeedbackDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        request.setRentRequestId(rentRequestId);
        request.setRentInfoId(rentInfoId);

        GetFeedbackDetailsResponse response = (GetFeedbackDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }
}
