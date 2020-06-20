package jvn.Zuul.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Zuul.config.RabbitMQConfiguration;
import jvn.Zuul.dto.message.Log;
import jvn.Zuul.dto.message.LogMessage;
import jvn.Zuul.serviceImpl.DigitalSignatureServiceImpl;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class LogProducer {

    private RabbitTemplate rabbitTemplate;

    private DigitalSignatureServiceImpl digitalSignatureService;

    public void send(Log log) {
        byte[] digitalSignature = digitalSignatureService.encrypt(log.toString().getBytes(StandardCharsets.UTF_8));
        LogMessage logMessage = new LogMessage(log.toString(), digitalSignature);
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.LOGS, jsonToString(logMessage));
    }

    private String jsonToString(LogMessage logMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(logMessage);
        } catch (IOException e) {
            return null;
        }
    }

    @Autowired
    public LogProducer(RabbitTemplate rabbitTemplate, DigitalSignatureServiceImpl digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.digitalSignatureService = digitalSignatureService;
    }

}
