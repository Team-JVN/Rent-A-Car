package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.repository.AdvertisementRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementConsumer {

    private ObjectMapper objectMapper;

    private AdvertisementRepository advertisementRepository;

    @RabbitListener(queues = RabbitMQConfiguration.ADVERTISEMENT_FOR_SEARCH)
    public void listen(String advertisementMessageStr) {
        Advertisement advertisement = stringToObject(advertisementMessageStr);
        advertisementRepository.save(advertisement);
    }

    @RabbitListener(queues = RabbitMQConfiguration.DELETED_ADVERTISEMENT)
    public void listen(Long advId) {
        Advertisement advertisement = advertisementRepository.findOneById(advId);
        advertisement.setLogicalStatus(LogicalStatus.DELETED);
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
