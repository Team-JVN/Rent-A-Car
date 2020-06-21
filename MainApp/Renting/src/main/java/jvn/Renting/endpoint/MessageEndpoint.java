package jvn.Renting.endpoint;

import jvn.Renting.client.UserClient;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.response.UserInfoDTO;
import jvn.Renting.dto.soap.message.*;
import jvn.Renting.dto.soap.message.MessageDetails;
import jvn.Renting.dto.soap.rentrequest.*;
import jvn.Renting.mapper.MessageDetailsMapper;
import jvn.Renting.model.Message;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.service.MessageService;
import jvn.Renting.service.RentRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Endpoint
public class MessageEndpoint {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String NAMESPACE_URI = "http://www.soap.dto/message";


    private MessageDetailsMapper messageDetailsMapper;

    private MessageService messageService;

    private UserClient userClient;

    private LogProducer logProducer;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createMessageRequest")
    @ResponsePayload
    public CreateMessageResponse createMessage(@RequestPayload CreateMessageRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }
        MessageDetails messageDetails = request.getMessageDetails();
        messageDetails.setId(null);
        try {
            Message message = messageService.createMessage(messageDetailsMapper.toEntity(messageDetails),
                    request.getRentRequestId(), dto.getId(), dto.getEmail());
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CRQ",
                    String.format("[SOAP] User %s successfully created message %s", dto.getId(), message.getId())));
            messageDetails = messageDetailsMapper.toDto(message);
        } catch (DateTimeParseException e) {
            return null;
        }

        CreateMessageResponse response = new CreateMessageResponse();
        response.setMessageDetails(messageDetails);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllMessagesRequest")
    @ResponsePayload
    public GetAllMessagesDetailsResponse getAllMessages(@RequestPayload GetAllMessagesDetailsRequest request) {
        UserInfoDTO dto = userClient.getUser(request.getEmail());
        if (dto == null) {
            return null;
        }

        List<MessageDetails> list = messageService.getMessages(request.getRentRequestId(), dto.getId())
                .stream().map(messageDetailsMapper::toDto)
                .collect(Collectors.toList());
        GetAllMessagesDetailsResponse response = new GetAllMessagesDetailsResponse();
        response.getMessageDetails().addAll(list);
        return response;
    }

    @Autowired
    public MessageEndpoint(MessageDetailsMapper messageDetailsMapper, MessageService messageService, UserClient userClient, LogProducer logProducer) {
        this.messageDetailsMapper = messageDetailsMapper;
        this.messageService = messageService;
        this.userClient = userClient;
        this.logProducer = logProducer;
    }

}
