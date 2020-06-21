package jvn.RentACar.client;

import jvn.RentACar.dto.soap.message.CreateMessageRequest;
import jvn.RentACar.dto.soap.message.CreateMessageResponse;
import jvn.RentACar.dto.soap.message.GetAllMessagesDetailsRequest;
import jvn.RentACar.dto.soap.message.GetAllMessagesDetailsResponse;
import jvn.RentACar.mapper.CommentDetailsMapper;
import jvn.RentACar.mapper.CommentDtoMapper;
import jvn.RentACar.mapper.MessageDetailsMapper;
import jvn.RentACar.mapper.RentRequestDetailsMapper;
import jvn.RentACar.model.Message;
import jvn.RentACar.model.User;
import jvn.RentACar.service.RentInfoService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class MessageClient  extends WebServiceGatewaySupport {
    @Autowired
    private UserService userService;

    @Autowired
    private RentRequestDetailsMapper rentRequestDetailsMapper;

    @Autowired
    private CommentDetailsMapper commentDetailsMapper;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private RentInfoService rentInfoService;

    @Autowired
    private MessageDetailsMapper messageDetailsMapper;

    public CreateMessageResponse createMessage (Long rentRequestId, Message message){
        CreateMessageRequest request = new CreateMessageRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setEmail(user.getEmail());
        request.setRentRequestId(rentRequestId);
        request.setMessageDetails(messageDetailsMapper.toDto(message));

        CreateMessageResponse response = (CreateMessageResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

    public GetAllMessagesDetailsResponse getAllMessages(Long rentRequestId){
        GetAllMessagesDetailsRequest request = new GetAllMessagesDetailsRequest();
        User user = userService.getLoginUser();
        if (user == null) {
            return null;
        }
        request.setRentRequestId(rentRequestId);
        request.setEmail(user.getEmail());

        GetAllMessagesDetailsResponse response = (GetAllMessagesDetailsResponse) getWebServiceTemplate()
                .marshalSendAndReceive(request);
        return response;
    }

}
