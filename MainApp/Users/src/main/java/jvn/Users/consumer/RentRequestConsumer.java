package jvn.Users.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.dto.message.ClientIdSignedDTO;
import jvn.Users.dto.message.Log;
import jvn.Users.dto.message.RentRequestMessageDTO;
import jvn.Users.dto.message.RentRequestSignedDTO;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;
import jvn.Users.producer.LogProducer;
import jvn.Users.repository.ClientRepository;
import jvn.Users.service.DigitalSignatureService;
import jvn.Users.service.EmailNotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RentRequestConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String RENTING_ALIAS = "renting";

    private ClientRepository clientRepository;

    private ObjectMapper objectMapper;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.CANCELED_RESERVATION)
    public void listen(String clientIdStr) {
        ClientIdSignedDTO clientIdSignedDTO = stringToClientIdSignedDTO(clientIdStr);
        if (digitalSignatureService.decrypt(RENTING_ALIAS, clientIdSignedDTO.getClientIdBytes(), clientIdSignedDTO.getDigitalSignature())) {
            Long clientId = bytesToLong(clientIdSignedDTO.getClientIdBytes());
            Client client = clientRepository.findOneByIdAndStatusNot(clientId, ClientStatus.DELETED);
            if (client != null) {
                int canceledReservationCounter = client.getCanceledReservationCounter() + 1;
                client.setCanceledReservationCounter(canceledReservationCounter);
                clientRepository.save(client);
                logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "ECL", String.format("Increased canceled reservations counter for user %s", client.getId())));
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.REJECTED_RESERVATION)
    public void listenRejectedReservation(String message) {
        RentRequestSignedDTO rentRequestSignedDTO = stringToRentRequestSignedDTO(message);
        if (digitalSignatureService.decrypt(RENTING_ALIAS, rentRequestSignedDTO.getRentRequestBytes(), rentRequestSignedDTO.getDigitalSignature())) {
            RentRequestMessageDTO rentRequestMessageDTO = bytesToRentRequestMessageDTO(rentRequestSignedDTO.getRentRequestBytes());
            if (rentRequestMessageDTO != null) {
                Client client = clientRepository.findOneByIdAndStatusNot(rentRequestMessageDTO.getClientId(), ClientStatus.DELETED);
                if (client != null) {
                    composeAndSendRejectedRentRequest(client.getEmail(), rentRequestMessageDTO.getRentRequestId());
                }
            }
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.ACCEPTED_RESERVATION)
    public void listenAcceptedReservation(String message) {
        RentRequestSignedDTO rentRequestSignedDTO = stringToRentRequestSignedDTO(message);
        if (digitalSignatureService.decrypt(RENTING_ALIAS, rentRequestSignedDTO.getRentRequestBytes(), rentRequestSignedDTO.getDigitalSignature())) {
            RentRequestMessageDTO rentRequestMessageDTO = bytesToRentRequestMessageDTO(rentRequestSignedDTO.getRentRequestBytes());
            if (rentRequestMessageDTO != null) {
                Client client = clientRepository.findOneByIdAndStatusNot(rentRequestMessageDTO.getClientId(), ClientStatus.DELETED);
                if (client != null) {
                    composeAndSendAcceptedRentRequest(client.getEmail(), rentRequestMessageDTO.getRentRequestId());
                }
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

    private ClientIdSignedDTO stringToClientIdSignedDTO(String clientIdStr) {
        try {
            return objectMapper.readValue(clientIdStr, ClientIdSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Long bytesToLong(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, Long.class);
        } catch (IOException e) {
            return null;
        }
    }

    private RentRequestSignedDTO stringToRentRequestSignedDTO(String message) {
        try {
            return objectMapper.readValue(message, RentRequestSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private RentRequestMessageDTO bytesToRentRequestMessageDTO(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, RentRequestMessageDTO.class);
        } catch (IOException e) {
            return null;
        }
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    @Autowired
    public RentRequestConsumer(ObjectMapper objectMapper, ClientRepository clientRepository, EmailNotificationService emailNotificationService,
                               Environment environment, LogProducer logProducer, DigitalSignatureService digitalSignatureService) {
        this.objectMapper = objectMapper;
        this.clientRepository = clientRepository;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
