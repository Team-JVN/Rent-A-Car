package jvn.Users.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.enumeration.ClientStatus;
import jvn.Users.model.Client;
import jvn.Users.repository.ClientRepository;
import jvn.Users.service.EmailNotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CommentConsumer {

    private ClientRepository clientRepository;

    private ObjectMapper objectMapper;

    private EmailNotificationService emailNotificationService;

    private Environment environment;

    @RabbitListener(queues = RabbitMQConfiguration.REJECTED_COMMENT)
    public void listenRejectedComment(Long clientId) {
//        Long clientId = stringToObject(message);
        System.out.println("listenRejectedComment");
        if (clientId != null) {
            System.out.println("searching for client");
            Client client = clientRepository.findOneByIdAndStatusNot(clientId, ClientStatus.DELETED);
            System.out.println("found client");
            if(client != null){
                System.out.println("client is not null");
                Integer count = client.getRejectedCommentsCounter() + 1;
                client.setRejectedCommentsCounter(count);
                System.out.println("UPDATING REJECTED COMMENTS COUNTER>>>");
                clientRepository.save(client);
                System.out.println("UPDATED REJECTED COMMENTS COUNTER<<<<");
            }

        }
    }

    private Long stringToObject(String message) {
        try {
            return objectMapper.readValue(message, Long.class);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    private String getLocalhostURL() {
        return environment.getProperty("LOCALHOST_URL");
    }

    @Autowired
    public CommentConsumer(ClientRepository clientRepository, ObjectMapper objectMapper, EmailNotificationService emailNotificationService, Environment environment) {
        this.clientRepository = clientRepository;
        this.objectMapper = objectMapper;
        this.emailNotificationService = emailNotificationService;
        this.environment = environment;
    }
}
