package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentProducer {

    public static final String REJECTED_COMMENT = "rejected-comment";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendRejectedComment(Long clientId) {
        rabbitTemplate.convertAndSend(REJECTED_COMMENT, jsonToString(clientId));
    }
    private String jsonToString(Long clientId) {
        try {
            return objectMapper.writeValueAsString(clientId);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public CommentProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
