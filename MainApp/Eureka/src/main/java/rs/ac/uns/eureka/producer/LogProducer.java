package rs.ac.uns.eureka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import rs.ac.uns.eureka.config.RabbitMQConfiguration;
import rs.ac.uns.eureka.message.Log;
import rs.ac.uns.eureka.message.LogSignedDTO;
import rs.ac.uns.eureka.service.DigitalSignatureService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class LogProducer {

    @Value("${KEYSTORE_ALIAS:eureka}")
    private String sender;

    private RabbitTemplate rabbitTemplate;

    private DigitalSignatureService digitalSignatureService;

    public void send(Log log) {
        byte[] digitalSignature = digitalSignatureService.encrypt(log.toString().getBytes(StandardCharsets.UTF_8));
        LogSignedDTO logSignedDTO = new LogSignedDTO(sender, log.toString(), digitalSignature);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.LOGS, jsonToString(logSignedDTO));
    }

    private String jsonToString(LogSignedDTO logSignedDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(logSignedDTO);
        } catch (IOException e) {
            return null;
        }
    }

    @Autowired
    public LogProducer(RabbitTemplate rabbitTemplate, DigitalSignatureService digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.digitalSignatureService = digitalSignatureService;
    }

}
