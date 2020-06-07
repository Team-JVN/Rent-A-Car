package jvn.Users.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.dto.message.OwnerMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private static final String USERS_FOR_SEARCH = "users-for-search";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendMessageForSearch(OwnerMessageDTO ownerMessageDTO) {
        rabbitTemplate.convertAndSend(USERS_FOR_SEARCH, jsonToString(ownerMessageDTO));
    }

    private String jsonToString(OwnerMessageDTO ownerMessageDTO) {
        try {
            return objectMapper.writeValueAsString(ownerMessageDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public UserProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
