package jvn.Renting.serviceImpl;

import jvn.Renting.client.AdvertisementClient;
import jvn.Renting.dto.both.AdvertisementWithIdsDTO;
import jvn.Renting.model.Message;
import jvn.Renting.model.RentInfo;
import jvn.Renting.model.RentRequest;
import jvn.Renting.repository.MessageRepository;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private RentRequestRepository rentRequestRepository;

    private MessageRepository messageRepository;

    private AdvertisementClient advertisementClient;

    @Override
    public Message createMessage(Message message, Long id, Long userId, String userEmail) {
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        message.setRentRequest(rentRequest);
        message.setSenderId(userId);
        message.setSenderEmail(userEmail);
//        rentRequest.getMessages().add(message);
//        List<Message> messages = getMessages(id,userId);
//        messages.add(message);
//        rentRequest.setMessages(new HashSet<>(messages));
        Message retVal = messageRepository.save(message);

//        try {
//            this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageDtoMapper.toDto(message));
//        } catch (Exception e) {
//            throw new InvalidRentRequestDataException("Socket error", HttpStatus.BAD_REQUEST);
//        }

//        rentRequestRepository.save(rentRequest);
        return retVal;
    }

    @Override
    public List<Message> getMessages(Long id, Long userId) {
        RentRequest rentRequest = rentRequestRepository.findOneById(id);
        List<Message> messages = new ArrayList<>();
        List<Message>currMessages = messageRepository.findByRentRequestId(id);
        List<RentInfo> rentInfos = new ArrayList<>(rentRequest.getRentInfos());
        AdvertisementWithIdsDTO advertisementWithIdsDTO = advertisementClient.getOne(rentInfos.get(0).getAdvertisement());
        for(Message message: currMessages){
            if(message.getSenderId().equals(userId) || rentRequest.getCreatedBy().equals(userId)
                    || rentRequest.getClient().equals(userId) || advertisementWithIdsDTO.getOwner().equals(userId)){
                messages.add(message);
            }
        }
        return messages;
    }

    @Autowired
    public MessageServiceImpl(RentRequestRepository rentRequestRepository, MessageRepository messageRepository,
                              AdvertisementClient advertisementClient) {
        this.rentRequestRepository = rentRequestRepository;
        this.messageRepository = messageRepository;
        this.advertisementClient = advertisementClient;
    }
}
