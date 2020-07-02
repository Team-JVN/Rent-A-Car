package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.CarMessageDTO;
import jvn.SearchService.dto.message.Log;
import jvn.SearchService.dto.message.CarAverageRatingDTO;
import jvn.SearchService.model.Car;
import jvn.SearchService.producer.LogProducer;
import jvn.SearchService.repository.CarRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private ObjectMapper objectMapper;

    private CarRepository carRepository;

    private LogProducer logProducer;

    @RabbitListener(queues = RabbitMQConfiguration.EDIT_PARTIAL_CAR)
    public void listenEditPartialCar(String carMessageStr) {
        CarMessageDTO carMessageDTO = stringToObject(carMessageStr);
        Car car = carRepository.findOneById(carMessageDTO.getId());
        car.setMileageInKm(carMessageDTO.getMileageInKm());
        car.setKidsSeats(carMessageDTO.getKidsSeats());
        car.setAvailableTracking(carMessageDTO.getAvailableTracking());

        carRepository.save(car);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ECA", String.format("Successfully edited car %s", car.getId())));
    }

    private CarMessageDTO stringToObject(String carMessageStr) {
        try {
            return objectMapper.readValue(carMessageStr, CarMessageDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.CAR_RATING_FOR_SEARCH_SERVICE)
    public void listenUpdateCarRating(String carMessageStr) {
        CarAverageRatingDTO carAverageRatingDTO = stringToObjectCarAverageRatingDTO(carMessageStr);
        Car car = carRepository.findOneById(carAverageRatingDTO.getCarId());
        car.setAvgRating(carAverageRatingDTO.getRating());
        carRepository.save(car);
    }

    private CarAverageRatingDTO stringToObjectCarAverageRatingDTO(String carMessageStr) {
        try {
            return objectMapper.readValue(carMessageStr, CarAverageRatingDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public CarConsumer(ObjectMapper objectMapper, CarRepository carRepository, LogProducer logProducer) {
        this.objectMapper = objectMapper;
        this.carRepository = carRepository;
        this.logProducer = logProducer;
    }
}
