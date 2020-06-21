package jvn.Advertisements.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Advertisements.dto.both.PriceListDTO;
import jvn.Advertisements.dto.message.*;
import jvn.Advertisements.dto.request.AdvertisementEditDTO;
import jvn.Advertisements.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

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

    private DigitalSignatureService digitalSignatureService;

    public void sendMessageForSearch(AdvertisementMessageDTO advertisementMessageDTO) {
        byte[] advertisementBytes = convertToBytes(advertisementMessageDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(advertisementBytes);
        AdvertisementSignedDTO advertisementSignedDTO = new AdvertisementSignedDTO(advertisementBytes, digitalSignature);
        rabbitTemplate.convertAndSend(ADVERTISEMENT_FOR_SEARCH, convertToString(advertisementSignedDTO));
    }

    public void sendMessageForSearch(AdvertisementEditDTO advertisementEditDTO) {
        byte[] advertisementEditBytes = convertToBytes(advertisementEditDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(advertisementEditBytes);
        AdvertisementEditSignedDTO advertisementEditSignedDTO = new AdvertisementEditSignedDTO(advertisementEditBytes, digitalSignature);
        rabbitTemplate.convertAndSend(EDIT_PARTIAL_ADVERTISEMENT, convertToString(advertisementEditSignedDTO));
    }

    public void sendMessageForSearch(Long advId) {
        byte[] advIdBytes = convertToBytes(advId);
        byte[] digitalSignature = digitalSignatureService.encrypt(advIdBytes);
        AdvIdSignedDTO advIdSignedDTO = new AdvIdSignedDTO(advIdBytes, digitalSignature);
        rabbitTemplate.convertAndSend(DELETED_ADVERTISEMENT, convertToString(advIdSignedDTO));
    }

    public void sendMessageForSearch(PriceListDTO priceListDTO) {
        byte[] priceListBytes = convertToBytes(priceListDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(priceListBytes);
        PriceListSignedDTO priceListSignedDTO = new PriceListSignedDTO(priceListBytes, digitalSignature);
        rabbitTemplate.convertAndSend(EDIT_PRICE_LIST_ADVERTISEMENT, convertToString(priceListSignedDTO));
    }

    public void sendMessageToRentingService(Long advId) {
        rabbitTemplate.convertAndSend(REJECT_ALL_REQUESTS, advId);
    }


    private byte[] convertToBytes(AdvertisementMessageDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", AdvertisementMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(AdvertisementSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", AdvertisementSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    private byte[] convertToBytes(Long obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", Long.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(AdvIdSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", AdvertisementSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    private byte[] convertToBytes(AdvertisementEditDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", AdvertisementEditDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(AdvertisementEditSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", AdvertisementEditSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    private byte[] convertToBytes(PriceListDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", PriceListDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(PriceListSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", PriceListSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public AdvertisementProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer,
                                 DigitalSignatureService digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
