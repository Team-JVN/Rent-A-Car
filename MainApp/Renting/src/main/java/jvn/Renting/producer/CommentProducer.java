package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.config.RabbitMQConfiguration;
import jvn.Renting.dto.message.UpdateCarRatingDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentProducer {

    public static final String REJECTED_COMMENT = "rejected-comment";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendRejectedComment(Long userId) {
        rabbitTemplate.convertAndSend(REJECTED_COMMENT, userId);
    }

    public void sendUpdateCarRating(Long carId, Integer rating) {
        UpdateCarRatingDTO updateCarRatingDTO = new UpdateCarRatingDTO(carId, rating);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.CAR_RATING, jsonToString(updateCarRatingDTO));
    }

    private String jsonToString(UpdateCarRatingDTO updateCarRatingDTO) {
        try {
            return objectMapper.writeValueAsString(updateCarRatingDTO);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public CommentProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
