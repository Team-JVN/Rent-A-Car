package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.message.*;
import jvn.Renting.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentRequestProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String CANCELED_RESERVATION = "canceled-reservation";

    private static final String REJECTED_RESERVATION = "rejected-reservation";

    private static final String ACCEPTED_RESERVATION = "accepted-reservation";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    public void sendCanceledReservation(Long clientId) {
        byte[] clientIdBytes = convertToBytes(clientId);
        byte[] digitalSignature = digitalSignatureService.encrypt(clientIdBytes);
        ClientIdSignedDTO clientIdSignedDTO = new ClientIdSignedDTO(clientIdBytes, digitalSignature);
        rabbitTemplate.convertAndSend(CANCELED_RESERVATION, convertToString(clientIdSignedDTO));
    }

    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        byte[] rentRequestBytes = convertToBytes(new RentRequestMessageDTO(clientId, rentRequestId));
        byte[] digitalSignature = digitalSignatureService.encrypt(rentRequestBytes);
        RentRequestSignedDTO rentRequestSignedDTO = new RentRequestSignedDTO(rentRequestBytes, digitalSignature);
        rabbitTemplate.convertAndSend(REJECTED_RESERVATION, convertToString(rentRequestSignedDTO));
    }

    public void sendAcceptedReservation(Long clientId, Long rentRequestId) {
        byte[] rentRequestBytes = convertToBytes(new RentRequestMessageDTO(clientId, rentRequestId));
        byte[] digitalSignature = digitalSignatureService.encrypt(rentRequestBytes);
        RentRequestSignedDTO rentRequestSignedDTO = new RentRequestSignedDTO(rentRequestBytes, digitalSignature);
        rabbitTemplate.convertAndSend(ACCEPTED_RESERVATION, convertToString(rentRequestSignedDTO));
    }


    private byte[] convertToBytes(Long obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", Long.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(ClientIdSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", ClientIdSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    private byte[] convertToBytes(RentRequestMessageDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", RentRequestMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(RentRequestSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", RentRequestSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public RentRequestProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer,
                               DigitalSignatureService digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
