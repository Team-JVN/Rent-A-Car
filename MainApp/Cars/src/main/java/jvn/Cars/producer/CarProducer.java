package jvn.Cars.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.request.CarEditDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarProducer {

    private static final String EDIT_PARTIAL_CAR = "edit-partial-car";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendMessageForSearch(CarEditDTO carEditDTO) {
        rabbitTemplate.convertAndSend(EDIT_PARTIAL_CAR, jsonToString(carEditDTO));
    }

    private String jsonToString(CarEditDTO carEditDTO) {
        try {
            return objectMapper.writeValueAsString(carEditDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public CarProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
