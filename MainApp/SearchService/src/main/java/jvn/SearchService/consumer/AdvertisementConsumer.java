package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.AdvertisementEditDTO;
import jvn.SearchService.dto.PriceListDTO;
import jvn.SearchService.dto.message.AdvertisementSignedDTO;
import jvn.SearchService.dto.message.Log;
import jvn.SearchService.enumeration.LogicalStatus;
import jvn.SearchService.model.Advertisement;
import jvn.SearchService.model.PriceList;
import jvn.SearchService.producer.LogProducer;
import jvn.SearchService.repository.AdvertisementRepository;
import jvn.SearchService.repository.PriceListRepository;
import jvn.SearchService.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AdvertisementConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String ADVERTISEMENTS_ALIAS = "advertisements";

    private ObjectMapper objectMapper;

    private AdvertisementRepository advertisementRepository;

    private PriceListRepository priceListRepository;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.ADVERTISEMENT_FOR_SEARCH)
    public void listen(String advertisementMessageStr) {
        AdvertisementSignedDTO advertisementSignedDTO = stringToObject(advertisementMessageStr);
        if (digitalSignatureService.decrypt(ADVERTISEMENTS_ALIAS, advertisementSignedDTO.getAdvertisementBytes(), advertisementSignedDTO.getDigitalSignature())) {
            Advertisement advertisement = bytesToObject(advertisementSignedDTO.getAdvertisementBytes());
            advertisementRepository.save(advertisement);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CAD", String.format("Successfully created advertisement %s", advertisement.getId())));
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CCA", String.format("Successfully created car %s", advertisement.getCar().getId())));
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "CPL", String.format("Successfully created price list %s", advertisement.getPriceList().getId())));
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "COW", String.format("Successfully created owner %s", advertisement.getOwner().getId())));
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.DELETED_ADVERTISEMENT)
    public void listen(Long advId) {
        Advertisement advertisement = advertisementRepository.findOneById(advId);
        advertisement.setLogicalStatus(LogicalStatus.DELETED);
        advertisementRepository.save(advertisement);
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "DAD", String.format("Successfully deleted advertisement %s", advId)));
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
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EAD", String.format("Successfully edited advertisement %s", advertisement.getId())));
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
        logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EPL", String.format("Successfully edited price list %s", priceList.getId())));
    }

    private AdvertisementSignedDTO stringToObject(String advertisementMessageStr) {
        try {
            return objectMapper.readValue(advertisementMessageStr, AdvertisementSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private byte[] objectToBytes(Advertisement advertisementMessageDTO) {
        try {
            return objectMapper.writeValueAsBytes(advertisementMessageDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", Advertisement.class.getSimpleName())));
            return null;
        }
    }

    private Advertisement bytesToObject(byte[] advertisementBytes) {
        try {
            return objectMapper.readValue(advertisementBytes, Advertisement.class);
        } catch (IOException e) {
            return null;
        }
    }

    private AdvertisementEditDTO stringToObjectAdvEditDTO(String advertisementMessageStr) {
        try {
            return objectMapper.readValue(advertisementMessageStr, AdvertisementEditDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private PriceListDTO stringToObjectPriceListDTO(String message) {
        try {
            return objectMapper.readValue(message, PriceListDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public AdvertisementConsumer(ObjectMapper objectMapper, AdvertisementRepository advertisementRepository,
                                 PriceListRepository priceListRepository, LogProducer logProducer,
                                 DigitalSignatureService digitalSignatureService) {
        this.objectMapper = objectMapper;
        this.advertisementRepository = advertisementRepository;
        this.priceListRepository = priceListRepository;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
