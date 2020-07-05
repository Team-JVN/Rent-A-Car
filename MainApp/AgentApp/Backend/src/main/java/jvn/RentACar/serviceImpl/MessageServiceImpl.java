package jvn.RentACar.serviceImpl;

import jvn.RentACar.client.MessageClient;
import jvn.RentACar.dto.soap.message.CreateMessageResponse;
import jvn.RentACar.dto.soap.message.GetAllMessagesDetailsResponse;
import jvn.RentACar.dto.soap.message.MessageDetails;
import jvn.RentACar.mapper.MessageDetailsMapper;
import jvn.RentACar.model.*;
import jvn.RentACar.repository.MessageRepository;
import jvn.RentACar.repository.RentRequestRepository;
import jvn.RentACar.service.LogService;
import jvn.RentACar.service.MessageService;
import jvn.RentACar.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.ws.ServiceMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private MessageDetailsMapper messageDetailsMapper;

    private MessageRepository messageRepository;

    private UserService userService;

    private RentRequestRepository rentRequestRepository;

    private MessageClient messageClient;

    private LogService logService;

    @Override
    public Message createMessage(Message message, Long id) {
        User loggedInUser = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        message.setRentRequest(rentRequest);
        message.setSender(loggedInUser);
        rentRequest.getMessages().add(message);


//        try {
//            this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageDtoMapper.toDto(message));
//        } catch (Exception e) {
//            throw new InvalidRentRequestDataException("Socket error", HttpStatus.BAD_REQUEST);
//        }

//        rentRequestRepository.save(rentRequest);

        CreateMessageResponse createMessageResponse = messageClient.createMessage(rentRequest.getMainAppId(), message);

        MessageDetails details = createMessageResponse.getMessageDetails();
        if(details != null && details.getId() != null){
            message.setMainAppId(details.getId());
        }
        messageRepository.save(message);

        return message;
    }

    @Override
    public List<Message> getMessages(Long id) {
        synchronize();
        User loggedInUser = userService.getLoginUser();
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
//        synchronizeMessages(rentRequest.getMainAppId());
        List<Message> messages = new ArrayList<>();
        List<RentInfo> rentInfos = new ArrayList<>(rentRequest.getRentInfos());
        Long rentInfoId = rentInfos.get(0).getId();
        for(Message message: rentRequest.getMessages()){
            if(message.getSender().getId().equals(loggedInUser.getId()) || rentRequest.getCreatedBy().getId().equals(loggedInUser.getId())
                    || rentRequest.getClient().getId().equals(loggedInUser.getId()) || loggedInUser.getRole().getName().equals("ROLE_AGENT")){

                messages.add(message);
            }
        }
        return messages;
    }
    @Scheduled(cron = "0 40 0/3 * * ?")
    public void synchronize() {
        try {
            List<RentRequest> rentRequests = rentRequestRepository.findAll();
            for(RentRequest rentRequest: rentRequests){
                GetAllMessagesDetailsResponse response = messageClient.getAllMessages(rentRequest.getMainAppId());
                if (response == null) {
                    continue;
                }
                List<MessageDetails> messageDetails = response.getMessageDetails();
                if (messageDetails == null || messageDetails.isEmpty()) {
                    continue;
                }

                for (MessageDetails current : messageDetails) {
                    Message message = messageDetailsMapper.toEntity(current);
                    Message dbMessage = messageRepository.findByMainAppId(message.getMainAppId());
                    if (dbMessage == null) {
                        createSynchronize(message, rentRequest);
                    } else {
                        editSynchronize(message, dbMessage, rentRequest);
                    }
                }
                logService.write(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SYN",
                        "[SOAP] Messages are successfully synchronized"));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void createSynchronize(Message message, RentRequest rentRequest) {
        if (message.getSender() == null) {
            return;
        }
        message.setRentRequest(rentRequest);
        messageRepository.saveAndFlush(message);
    }
    private void editSynchronize(Message message, Message dbMessage, RentRequest rentRequest) {
        if (message.getSender() == null) {
            return;
        }
        dbMessage.setDateAndTime(message.getDateAndTime());
        dbMessage.setText(message.getText());
        dbMessage.setSender(message.getSender());
        dbMessage.setRentRequest(rentRequest);

        messageRepository.save(dbMessage);
    }
    @Autowired
    public MessageServiceImpl(MessageDetailsMapper messageDetailsMapper, MessageRepository messageRepository,
                              UserService userService, RentRequestRepository rentRequestRepository, MessageClient messageClient,
                              LogService logService) {
        this.messageDetailsMapper = messageDetailsMapper;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.rentRequestRepository = rentRequestRepository;
        this.messageClient = messageClient;
        this.logService = logService;
    }
}
