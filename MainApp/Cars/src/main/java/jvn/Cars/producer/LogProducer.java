package jvn.Cars.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.config.RabbitMQConfiguration;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.message.LogSignedDTO;
import jvn.Cars.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class LogProducer {

    @Value("${KEYSTORE_ALIAS:cars}")
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
