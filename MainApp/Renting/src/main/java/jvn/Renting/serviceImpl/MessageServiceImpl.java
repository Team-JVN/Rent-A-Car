package jvn.Renting.serviceImpl;

import jvn.Renting.model.Message;
import jvn.Renting.model.RentRequest;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private RentRequestRepository rentRequestRepository;

    @Override
    public Message createMessage(Message message, Long id, Long userId, String userEmail) {
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        message.setRentRequest(rentRequest);
        message.setSenderId(userId);
        message.setSenderEmail(userEmail);
        rentRequest.getMessages().add(message);


//        try {
//            this.simpMessagingTemplate.convertAndSend("/socket-publisher", messageDtoMapper.toDto(message));
//        } catch (Exception e) {
//            throw new InvalidRentRequestDataException("Socket error", HttpStatus.BAD_REQUEST);
//        }

        rentRequestRepository.save(rentRequest);
        return message;
    }

    @Override
    public List<Message> getMessages(Long id, Long userId) {
        RentRequest rentRequest = rentRequestRepository.findOneByIdAndCreatedByOrIdAndClient(id, userId, id, userId);
        List<Message> messages = new ArrayList<>();
        for(Message message: rentRequest.getMessages()){
            if(message.getSenderId().equals(userId) || rentRequest.getCreatedBy().equals(userId) || rentRequest.getClient().equals(userId)){
                messages.add(message);
            }
        }
        return messages;
    }

    @Autowired
    public MessageServiceImpl(RentRequestRepository rentRequestRepository) {
        this.rentRequestRepository = rentRequestRepository;
    }
}
