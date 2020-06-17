package jvn.Advertisements.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import jvn.Advertisements.dto.message.Log;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    private static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    private static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    private static final String EDIT_PARTIAL_ADVERTISEMENT = "advertisements-for-search-edit-partial-adv";

    private static final String EDIT_PRICE_LIST_ADVERTISEMENT = "advertisements-for-search-edit-price-list";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    public void sendMessageForSearch(AdvertisementMessageDTO advertisementMessageDTO) {
        rabbitTemplate.convertAndSend(ADVERTISEMENT_FOR_SEARCH, jsonToString(advertisementMessageDTO));
    }

    public void sendMessageForSearch(AdvertisementEditDTO advertisementEditDTO) {
        rabbitTemplate.convertAndSend(EDIT_PARTIAL_ADVERTISEMENT, jsonToString(advertisementEditDTO));
    }

    public void sendMessageForSearch(Long advId) {
        rabbitTemplate.convertAndSend(DELETED_ADVERTISEMENT, advId);
    }

    public void sendMessageForSearch(PriceListDTO priceListDTO) {
        rabbitTemplate.convertAndSend(EDIT_PRICE_LIST_ADVERTISEMENT, jsonToString(priceListDTO));
    }

    public void sendMessageToRentingService(Long advId) {
        rabbitTemplate.convertAndSend(REJECT_ALL_REQUESTS, advId);
    }

    private String jsonToString(AdvertisementMessageDTO advertisementMessageDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementMessageDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", AdvertisementMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    private String jsonToString(AdvertisementEditDTO advertisementEditDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementEditDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", AdvertisementEditDTO.class.getSimpleName())));
            return null;
        }
    }

    private String jsonToString(PriceListDTO priceListDTO) {
        try {
            return objectMapper.writeValueAsString(priceListDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", PriceListDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public AdvertisementProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
    }
}
