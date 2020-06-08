package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.CarMessageDTO;
import jvn.SearchService.model.Car;
import jvn.SearchService.repository.CarRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarConsumer {

    private ObjectMapper objectMapper;

    private CarRepository carRepository;

    @RabbitListener(queues = RabbitMQConfiguration.EDIT_PARTIAL_CAR)
    public void listenEditPartialCar(String carMessageStr) {
        CarMessageDTO carMessageDTO = stringToObject(carMessageStr);

        Car car = carRepository.findOneById(carMessageDTO.getId());
        car.setMileageInKm(carMessageDTO.getMileageInKm());
        car.setKidsSeats(carMessageDTO.getKidsSeats());
        car.setAvailableTracking(carMessageDTO.getAvailableTracking());

        carRepository.save(car);
    }

    private CarMessageDTO stringToObject(String carMessageStr) {
        try {
            return objectMapper.readValue(carMessageStr, CarMessageDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public CarConsumer(ObjectMapper objectMapper, CarRepository carRepository) {
        this.objectMapper = objectMapper;
        this.carRepository = carRepository;
    }
}
