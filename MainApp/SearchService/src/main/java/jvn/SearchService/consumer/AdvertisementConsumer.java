package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.repository.AdvertisementRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementConsumer {

    private ObjectMapper objectMapper;

    private AdvertisementRepository advertisementRepository;

    @RabbitListener(queues = RabbitMQConfiguration.QUEUE_NAME)
    public void listen(String advertisementMessageStr) {
        Advertisement advertisement = stringToObject(advertisementMessageStr);
        advertisementRepository.save(advertisement);
    }

    private Advertisement stringToObject(String advertisementMessageStr) {
        try {
            return objectMapper.readValue(advertisementMessageStr, Advertisement.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public AdvertisementConsumer(ObjectMapper objectMapper, AdvertisementRepository advertisementRepository) {
        this.objectMapper = objectMapper;
        this.advertisementRepository = advertisementRepository;
    }
}
