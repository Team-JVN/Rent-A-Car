package jvn.Cars.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import jvn.Cars.config.RabbitMQConfiguration;
import jvn.Cars.dto.message.UpdateCarMileageDTO;
import jvn.Cars.model.Car;
import jvn.Cars.repository.CarRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementConsumer {

    private ObjectMapper objectMapper;

    private CarRepository carRepository;

    @RabbitListener(queues = RabbitMQConfiguration.UPDATE_CAR_MILEAGE)
    public void listenUpdateCarMileage(String message) {

        UpdateCarMileageDTO updateCarMileageDTO = stringToObject(message);
        Car car = carRepository.findById(updateCarMileageDTO.getCarId()).orElse(null);
        if (car != null) {
            int mileage = car.getMileageInKm() + updateCarMileageDTO.getMadeMileage();
            car.setMileageInKm(mileage);
            carRepository.save(car);

        }
    }

    private UpdateCarMileageDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, UpdateCarMileageDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public AdvertisementConsumer(ObjectMapper objectMapper, CarRepository carRepository) {
        this.objectMapper = objectMapper;
        this.carRepository = carRepository;
    }
}
