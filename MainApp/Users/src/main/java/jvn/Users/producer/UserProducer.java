package jvn.Users.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.message.OwnerMessageDTO;
import jvn.Users.dto.message.OwnerSignedDTO;
import jvn.Users.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    public void sendMessageForSearch(OwnerMessageDTO ownerMessageDTO) {
        byte[] ownerBytes = convertToBytes(ownerMessageDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(ownerBytes);
        OwnerSignedDTO ownerSignedDTO = new OwnerSignedDTO(ownerBytes, digitalSignature);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.USERS_FOR_SEARCH, convertToString(ownerSignedDTO));
    }


    private byte[] convertToBytes(OwnerMessageDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", OwnerMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(OwnerSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", OwnerSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public UserProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer,
                        DigitalSignatureService digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
