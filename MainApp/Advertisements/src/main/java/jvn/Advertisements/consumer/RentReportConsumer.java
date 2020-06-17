package jvn.Advertisements.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.config.RabbitMQConfiguration;
import jvn.Advertisements.dto.message.RentReportMessageDTO;
import jvn.Advertisements.model.Advertisement;
import jvn.Advertisements.producer.AdvertisementProducer;
import jvn.Advertisements.repository.AdvertisementRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RentReportConsumer {

    private AdvertisementRepository advertisementRepository;

    private ObjectMapper objectMapper;

    private AdvertisementProducer advertisementProducer;

    @RabbitListener(queues = RabbitMQConfiguration.MILEAGE)
    public void listenCarUpdates(String message) {
        System.out.println("*********************LISTEN TO SOUND HERE IN MY HEART*********************");
        RentReportMessageDTO rentReportMessageDTO = stringToObject(message);
        if (rentReportMessageDTO.getAdvertisementId() != null) {
            Advertisement advertisement = advertisementRepository.findById(rentReportMessageDTO.getAdvertisementId()).orElse(null);
            if (advertisement != null) {
                sendCarUpdates(advertisement.getCar(), rentReportMessageDTO.getMadeMileage());
            }
        }
    }

    @Async
    public void sendCarUpdates(Long carId, Integer madeMileage){
        advertisementProducer.sendUpdateCarMileage(carId, madeMileage);
    }

    private RentReportMessageDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, RentReportMessageDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public RentReportConsumer(AdvertisementRepository advertisementRepository, ObjectMapper objectMapper,
                              AdvertisementProducer advertisementProducer) {
        this.advertisementRepository = advertisementRepository;
        this.objectMapper = objectMapper;
        this.advertisementProducer = advertisementProducer;
    }
}
