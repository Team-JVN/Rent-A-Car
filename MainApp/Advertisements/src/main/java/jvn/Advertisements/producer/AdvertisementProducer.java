package jvn.Advertisements.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.message.AdvertisementMessageDTO;
import jvn.Advertisements.dto.message.RentReportMessageDTO;
import jvn.Advertisements.dto.message.UpdateCarMileageDTO;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.image.ImageDecoder;

@Service
public class AdvertisementProducer {

    private static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    private static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    private static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    private static final String EDIT_PARTIAL_ADVERTISEMENT = "advertisements-for-search-edit-partial-adv";

    private static final String EDIT_PRICE_LIST_ADVERTISEMENT = "advertisements-for-search-edit-price-list";

    private static final String UPDATE_CAR_MILEAGE = "update-car-mileage";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

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

    public void sendUpdateCarMileage(Long carId, Integer madeMileage){
        System.out.println("sending car mileage");
        rabbitTemplate.convertAndSend(UPDATE_CAR_MILEAGE, jsonToString(new UpdateCarMileageDTO(carId, madeMileage)));
        System.out.println("sending car mileage2");

    }

    private String jsonToString(AdvertisementMessageDTO advertisementMessageDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementMessageDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private String jsonToString(AdvertisementEditDTO advertisementEditDTO) {
        try {
            return objectMapper.writeValueAsString(advertisementEditDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private String jsonToString(PriceListDTO priceListDTO) {
        try {
            return objectMapper.writeValueAsString(priceListDTO);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private String jsonToString(UpdateCarMileageDTO updateCarMileageDTO) {
        try {
            return objectMapper.writeValueAsString(updateCarMileageDTO);
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
