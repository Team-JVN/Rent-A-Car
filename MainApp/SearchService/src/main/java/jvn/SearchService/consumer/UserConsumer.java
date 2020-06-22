package jvn.SearchService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.SearchService.config.RabbitMQConfiguration;
import jvn.SearchService.dto.message.Log;
import jvn.SearchService.dto.message.OwnerMessageDTO;
import jvn.SearchService.dto.message.OwnerSignedDTO;
import jvn.SearchService.model.Owner;
import jvn.SearchService.producer.LogProducer;
import jvn.SearchService.repository.OwnerRepository;
import jvn.SearchService.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class UserConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String USERS_ALIAS = "users";

    private ObjectMapper objectMapper;

    private OwnerRepository ownerRepository;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.USERS_FOR_SEARCH)
    public void listen(String message) {
        OwnerSignedDTO ownerSignedDTO = stringToOwnerSignedDTO(message);
        if (digitalSignatureService.decrypt(USERS_ALIAS, ownerSignedDTO.getOwnerBytes(), ownerSignedDTO.getDigitalSignature())) {
            OwnerMessageDTO ownerMessageDTO = bytesToOwnerMessageDTO(ownerSignedDTO.getOwnerBytes());
            Owner owner = ownerRepository.findOneById(ownerMessageDTO.getId());
            if (owner != null) {
                owner.setName(ownerMessageDTO.getName());
                ownerRepository.save(owner);
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "EOW", String.format("Successfully edited owner %s", owner.getId())));
            }
        }
    }


    private OwnerSignedDTO stringToOwnerSignedDTO(String message) {
        try {
            return objectMapper.readValue(message, OwnerSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private OwnerMessageDTO bytesToOwnerMessageDTO(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, OwnerMessageDTO.class);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping byte array to %s failed", OwnerMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public UserConsumer(ObjectMapper objectMapper, OwnerRepository ownerRepository, LogProducer logProducer,
                        DigitalSignatureService digitalSignatureService) {
        this.objectMapper = objectMapper;
        this.ownerRepository = ownerRepository;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
