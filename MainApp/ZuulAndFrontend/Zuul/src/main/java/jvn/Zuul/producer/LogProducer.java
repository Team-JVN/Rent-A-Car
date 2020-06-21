package jvn.Zuul.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Zuul.config.RabbitMQConfiguration;
import jvn.Zuul.dto.message.Log;
import jvn.Zuul.dto.message.LogMessageDTO;
import jvn.Zuul.service.DigitalSignatureService;
import jvn.Zuul.serviceImpl.DigitalSignatureServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class LogProducer {

    @Value("${KEYSTORE_ALIAS:zuul}")
    private String sender;

    private RabbitTemplate rabbitTemplate;

    private DigitalSignatureService digitalSignatureService;

    public void send(Log log) {
        byte[] digitalSignature = digitalSignatureService.encrypt(log.toString().getBytes(StandardCharsets.UTF_8));
        LogMessageDTO logMessageDTO = new LogMessageDTO(sender, log.toString(), digitalSignature);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.LOGS, jsonToString(logMessageDTO));
    }

    private String jsonToString(LogMessageDTO logMessageDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(logMessageDTO);
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
