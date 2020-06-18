package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.Log;
import jvn.SearchService.dto.message.OwnerMessageDTO;
import jvn.SearchService.model.Owner;
import jvn.SearchService.producer.LogProducer;
import jvn.SearchService.repository.OwnerRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private ObjectMapper objectMapper;

    private OwnerRepository ownerRepository;

    private LogProducer logProducer;

    @RabbitListener(queues = RabbitMQConfiguration.USERS_FOR_SEARCH)
    public void listen(String message) {
        OwnerMessageDTO ownerMessageDTO = stringToObject(message);
        Owner owner = ownerRepository.findOneById(ownerMessageDTO.getId());
        if (owner != null) {
            owner.setName(ownerMessageDTO.getName());
            ownerRepository.save(owner);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EOW", String.format("Successfully edited owner %s", owner.getId())));
        }
    }

    private OwnerMessageDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, OwnerMessageDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public UserConsumer(ObjectMapper objectMapper, OwnerRepository ownerRepository, LogProducer logProducer) {
        this.objectMapper = objectMapper;
        this.ownerRepository = ownerRepository;
        this.logProducer = logProducer;
    }
}
