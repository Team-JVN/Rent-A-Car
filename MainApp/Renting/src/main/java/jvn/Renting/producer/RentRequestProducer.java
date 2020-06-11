package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.message.RentRequestMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentRequestProducer {

    private static final String CANCELED_RESERVATION = "canceled-reservation";

    private static final String REJECTED_RESERVATION = "rejected-reservation";

    private static final String ACCEPTED_RESERVATION = "accepted-reservation";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

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
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public RentRequestProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
