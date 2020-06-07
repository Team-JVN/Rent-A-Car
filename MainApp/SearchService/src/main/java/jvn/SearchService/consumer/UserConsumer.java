package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.OwnerMessageDTO;
import jvn.SearchService.model.Owner;
import jvn.SearchService.repository.OwnerRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {
    private ObjectMapper objectMapper;

    private OwnerRepository ownerRepository;

    @RabbitListener(queues = RabbitMQConfiguration.USERS_FOR_SEARCH)
    public void listen(String message) {
        OwnerMessageDTO ownerMessageDTO = stringToObject(message);
        Owner owner = ownerRepository.findOneById(ownerMessageDTO.getId());
        if (owner != null) {
            owner.setName(ownerMessageDTO.getName());
            ownerRepository.save(owner);
        }
    }

    private OwnerMessageDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, OwnerMessageDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public UserConsumer(ObjectMapper objectMapper, OwnerRepository ownerRepository) {
        this.objectMapper = objectMapper;
        this.ownerRepository = ownerRepository;
    }
}
