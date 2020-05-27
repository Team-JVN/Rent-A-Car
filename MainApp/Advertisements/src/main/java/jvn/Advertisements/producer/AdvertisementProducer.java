package jvn.Advertisements.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementProducer {

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void send(AdvertisementMessageDTO advertisementMessageDTO) {
        rabbitTemplate.convertAndSend("advertisements-for-search", jsonToString(advertisementMessageDTO));
    }

    private String jsonToString(AdvertisementMessageDTO advertisementMessageDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementMessageDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public AdvertisementProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
