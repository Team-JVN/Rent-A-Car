package jvn.Users.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.message.OwnerMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    public void sendMessageForSearch(OwnerMessageDTO ownerMessageDTO) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.USERS_FOR_SEARCH, jsonToString(ownerMessageDTO));
    }

    private String jsonToString(OwnerMessageDTO ownerMessageDTO) {
        try {
            return objectMapper.writeValueAsString(ownerMessageDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", OwnerMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public UserProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
    }
}
