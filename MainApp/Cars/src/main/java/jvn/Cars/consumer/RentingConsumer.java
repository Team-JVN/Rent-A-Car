package jvn.Cars.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.config.RabbitMQConfiguration;
import jvn.Cars.dto.message.UpdateCarRatingDTO;
import jvn.Cars.service.CarService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentingConsumer {
    private ObjectMapper objectMapper;

    private CarService carService;

    @RabbitListener(queues = RabbitMQConfiguration.CAR_RATING)
    public void listenUpdateCarMileage(String message) {
        UpdateCarRatingDTO updateCarRatingDTO = stringToObject(message);
        carService.updateCarRating(updateCarRatingDTO.getCarId(), updateCarRatingDTO.getRating());
    }

    private UpdateCarRatingDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, UpdateCarRatingDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public RentingConsumer(ObjectMapper objectMapper, CarService carService) {
        this.objectMapper = objectMapper;
        this.carService = carService;
    }
}
