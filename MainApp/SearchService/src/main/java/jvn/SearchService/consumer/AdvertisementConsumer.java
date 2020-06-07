package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.AdvertisementEditDTO;
import jvn.SearchService.dto.PriceListDTO;
import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.model.PriceList;
import jvn.SearchService.repository.AdvertisementRepository;
import jvn.SearchService.repository.PriceListRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdvertisementConsumer {

    private ObjectMapper objectMapper;

    private AdvertisementRepository advertisementRepository;

    private PriceListRepository priceListRepository;

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

    @RabbitListener(queues = RabbitMQConfiguration.EDIT_PARTIAL_ADVERTISEMENT)
    public void listenEditPartialAdv(String advertisementMessageStr) {
        AdvertisementEditDTO advertisementEditDTO = stringToObjectAdvEditDTO(advertisementMessageStr);
        Advertisement advertisement = advertisementRepository.findOneById(advertisementEditDTO.getId());
        PriceList priceList = priceListRepository.findOneById(advertisementEditDTO.getPriceList().getId());
        if (priceList == null) {
            PriceListDTO priceListDTO = advertisementEditDTO.getPriceList();
            priceList = new PriceList(priceListDTO.getId(), priceListDTO.getPricePerDay(), priceListDTO.getPricePerKm(), priceListDTO.getPriceForCDW());
        }
        advertisement.setPriceList(priceList);
        if (priceList.getPricePerKm() == null) {
            advertisement.setKilometresLimit(null);
        } else {
            advertisement.setKilometresLimit(advertisementEditDTO.getKilometresLimit());
        }
        if (priceList.getPriceForCDW() != null) {
            advertisement.setCDW(true);
        } else {
            advertisement.setCDW(false);
        }

        advertisement.setDiscount(advertisementEditDTO.getDiscount());
        advertisementRepository.save(advertisement);
    }

    @RabbitListener(queues = RabbitMQConfiguration.EDIT_PRICE_LIST_ADVERTISEMENT)
    public void listenEditPriceList(String message) {
        PriceListDTO priceListDTO = stringToObjectPriceListDTO(message);
        PriceList priceList = priceListRepository.findOneById(priceListDTO.getId());
        if (priceList == null) {
            priceList = new PriceList();
            priceList.setId(priceListDTO.getId());
        }
        priceList.setPricePerDay(priceListDTO.getPricePerDay());
        priceList.setPricePerKm(priceListDTO.getPricePerKm());
        priceList.setPriceForCDW(priceListDTO.getPriceForCDW());
        priceListRepository.save(priceList);
    }

    private Advertisement stringToObject(String advertisementMessageStr) {
        try {
            return objectMapper.readValue(advertisementMessageStr, Advertisement.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private AdvertisementEditDTO stringToObjectAdvEditDTO(String advertisementMessageStr) {
        try {
            return objectMapper.readValue(advertisementMessageStr, AdvertisementEditDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private PriceListDTO stringToObjectPriceListDTO(String message) {
        try {
            return objectMapper.readValue(message, PriceListDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public AdvertisementConsumer(ObjectMapper objectMapper, AdvertisementRepository advertisementRepository, PriceListRepository priceListRepository) {
        this.objectMapper = objectMapper;
        this.advertisementRepository = advertisementRepository;
        this.priceListRepository = priceListRepository;
    }
}
