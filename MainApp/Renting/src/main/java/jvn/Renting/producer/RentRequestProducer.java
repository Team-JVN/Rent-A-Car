package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.message.Log;
import jvn.Renting.dto.message.RentRequestMessageDTO;
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

    public void sendCanceledReservation(Long clientId) {
        rabbitTemplate.convertAndSend(CANCELED_RESERVATION, clientId);
    }

    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        rabbitTemplate.convertAndSend(REJECTED_RESERVATION, jsonToString(new RentRequestMessageDTO(clientId, rentRequestId)));
    }

    public void sendAcceptedReservation(Long clientId, Long rentRequestId) {
        rabbitTemplate.convertAndSend(ACCEPTED_RESERVATION, jsonToString(new RentRequestMessageDTO(clientId, rentRequestId)));
    }

    private String jsonToString(RentRequestMessageDTO rentRequestMessageDTO) {
        try {
            return objectMapper.writeValueAsString(rentRequestMessageDTO);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", RentRequestMessageDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public RentRequestProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
    }
}
