package jvn.Advertisements.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementProducer {

    private static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    private static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    private static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendMessageForSearch(AdvertisementMessageDTO advertisementMessageDTO) {
        rabbitTemplate.convertAndSend(ADVERTISEMENT_FOR_SEARCH, jsonToString(advertisementMessageDTO));
    }

    public void sendMessageToRentingService(Long advId) {
        rabbitTemplate.convertAndSend(REJECT_ALL_REQUESTS, advId);
    }

    public void sendMessageForSearch(Long advId) {
        rabbitTemplate.convertAndSend(DELETED_ADVERTISEMENT, advId);
    }

    private String jsonToString(AdvertisementMessageDTO advertisementMessageDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementMessageDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public AdvertisementProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
