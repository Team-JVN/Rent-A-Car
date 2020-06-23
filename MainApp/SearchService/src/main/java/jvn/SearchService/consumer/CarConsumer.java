package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.CarEditSignedDTO;
import jvn.SearchService.dto.message.CarMessageDTO;
import jvn.SearchService.dto.message.Log;
import jvn.SearchService.model.Car;
import jvn.SearchService.producer.LogProducer;
import jvn.SearchService.repository.CarRepository;
import jvn.SearchService.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;

@Service
public class CarConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String CARS_ALIAS = "cars";

    private ObjectMapper objectMapper;

    private CarRepository carRepository;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.EDIT_PARTIAL_CAR)
    public void listenEditPartialCar(String carMessageStr) {
        CarEditSignedDTO carEditSignedDTO = stringToCarEditSignedDTO(carMessageStr);
        if (digitalSignatureService.decrypt(CARS_ALIAS, carEditSignedDTO.getCarEditBytes(), carEditSignedDTO.getDigitalSignature())) {
            CarMessageDTO carMessageDTO = bytesToCarMessageDTO(carEditSignedDTO.getCarEditBytes());
            Car car = carRepository.findOneById(carMessageDTO.getId());
            car.setMileageInKm(carMessageDTO.getMileageInKm());
            car.setKidsSeats(carMessageDTO.getKidsSeats());
            car.setAvailableTracking(carMessageDTO.getAvailableTracking());
            car.setPictures(new HashSet<>(carMessageDTO.getPictures()));
            carRepository.save(car);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ECA", String.format("Successfully edited car %s", car.getId())));
        }
    }

    private CarEditSignedDTO stringToCarEditSignedDTO(String message) {
        try {
            return objectMapper.readValue(message, CarEditSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private CarMessageDTO bytesToCarMessageDTO(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, CarMessageDTO.class);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping byte array to %s failed", CarMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public CarConsumer(ObjectMapper objectMapper, CarRepository carRepository, LogProducer logProducer,
                       DigitalSignatureService digitalSignatureService) {
        this.objectMapper = objectMapper;
        this.carRepository = carRepository;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
