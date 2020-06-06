package jvn.Users.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.dto.message.RentRequestMessageDTO;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;
import jvn.Users.repository.ClientRepository;
import jvn.Users.service.EmailNotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class RentRequestConsumer {

    private ClientRepository clientRepository;

    private ObjectMapper objectMapper;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    @RabbitListener(queues = RabbitMQConfiguration.CANCELED_RESERVATION)
    public void listen(Long clientId) {
        Client client = clientRepository.findOneByIdAndStatusNot(clientId, ClientStatus.DELETED);
        if (client != null) {
            int canceledReservationCounter = client.getCanceledReservationCounter() + 1;
            client.setCanceledReservationCounter(canceledReservationCounter);
            clientRepository.save(client);
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.REJECTED_RESERVATION)
    public void listenRejectedReservation(String message) {
        RentRequestMessageDTO rentRequestMessageDTO = stringToObject(message);
        if (rentRequestMessageDTO != null) {
            Client client = clientRepository.findOneByIdAndStatusNot(rentRequestMessageDTO.getClientId(), ClientStatus.DELETED);
            if (client != null) {
                composeAndSendRejectedRentRequest(client.getEmail(), rentRequestMessageDTO.getRentRequestId());
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.ACCEPTED_RESERVATION)
    public void listenAcceptedReservation(String message) {
        RentRequestMessageDTO rentRequestMessageDTO = stringToObject(message);
        if (rentRequestMessageDTO != null) {
            Client client = clientRepository.findOneByIdAndStatusNot(rentRequestMessageDTO.getClientId(), ClientStatus.DELETED);
            if (client != null) {
                composeAndSendAcceptedRentRequest(client.getEmail(), rentRequestMessageDTO.getRentRequestId());
            }
        }
    }

    private void composeAndSendRejectedRentRequest(String recipientEmail, Long rentRequestId) {
        String subject = "Rejected rent request";
        StringBuilder sb = new StringBuilder();
        sb.append("Your rent request is rejected by advertisement's owner");
        sb.append(System.lineSeparator());
        sb.append("To see rejected rent request click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("client-rent-request/");
        sb.append(rentRequestId);
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private void composeAndSendAcceptedRentRequest(String recipientEmail, Long rentRequestId) {
        String subject = "Accepted rent request";
        StringBuilder sb = new StringBuilder();
        sb.append("Your rent request is accepted by advertisement's owner");
        sb.append(System.lineSeparator());
        sb.append("To see accepted rent request click the following link:");
        sb.append(System.lineSeparator());
        sb.append(getLocalhostURL());
        sb.append("client-rent-request/");
        sb.append(rentRequestId);
        sb.append(System.lineSeparator());
        String text = sb.toString();
        emailNotificationService.sendEmail(recipientEmail, subject, text);
    }

    private RentRequestMessageDTO stringToObject(String message) {
        try {
            return objectMapper.readValue(message, RentRequestMessageDTO.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    @Autowired
    public RentRequestConsumer(ObjectMapper objectMapper, ClientRepository clientRepository, EmailNotificationService emailNotificationService,
                               Environment environment) {
        this.objectMapper = objectMapper;
        this.clientRepository = clientRepository;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
    }
}
