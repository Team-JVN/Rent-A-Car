package jvn.Cars.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.config.RabbitMQConfiguration;
import jvn.Cars.dto.message.CarAverageRatingDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.message.UpdateCarRatingDTO;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.dto.request.CarEditForSearchDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String EDIT_PARTIAL_CAR = "edit-partial-car";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    public void sendMessageForSearch(CarEditForSearchDTO carEditDTO) {
        rabbitTemplate.convertAndSend(EDIT_PARTIAL_CAR, jsonToString(carEditDTO));
    }

    public void sendMessageForSearchToUpdateRating(Long carId, Double avgRating) {
        CarAverageRatingDTO updateCarRatingDTO = new CarAverageRatingDTO(carId, avgRating);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.CAR_RATING_FOR_SEARCH_SERVICE, jsonToStringUpdateCarRatingDTO(updateCarRatingDTO));
    }

    private String jsonToString(CarEditForSearchDTO carEditDTO) {
        try {
            return objectMapper.writeValueAsString(carEditDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", CarEditForSearchDTO.class.getSimpleName())));
            return null;
        }
    }

    private String jsonToStringUpdateCarRatingDTO(CarAverageRatingDTO updateCarRatingDTO) {
        try {
            return objectMapper.writeValueAsString(updateCarRatingDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", CarAverageRatingDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public CarProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
    }
}
