package jvn.Logger.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Logger.config.ApplicationConfiguration;
import jvn.Logger.config.RabbitMQConfiguration;
import jvn.Logger.model.Log;
import jvn.Logger.model.LogMessageDTO;
import jvn.Logger.repository.FileRepository;
import jvn.Logger.serviceImpl.DigitalSignatureServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Service
public class LogConsumer {

    private FileRepository<Log> repository;

    private ApplicationConfiguration configuration;

    private DigitalSignatureServiceImpl digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.LOGS)
    public void write(String logMessageStr) {
        LogMessageDTO logMessageDTO = stringToObject(logMessageStr);
        if (digitalSignatureService.decrypt(logMessageDTO.getSender(), logMessageDTO.getLog().getBytes(StandardCharsets.UTF_8), logMessageDTO.getDigitalSignature())) {
            try {
                repository.write(Paths.get(configuration.getLogStorage()), Log.parse(logMessageDTO.getLog()));
                System.out.println(logMessageDTO.getLog());
            } catch (IOException e) {
                System.out.println("Cannot write log message to a file.");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
/*
    @RabbitListener(queues = RabbitMQConfiguration.LOGS)
    public void write(String logStr) {
        System.out.println(logStr);
        try {
            repository.write(Paths.get(configuration.getLogStorage()), Log.parse(logStr));
        } catch (IOException e) {
            System.out.println("Cannot write log message to a file.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
*/

    private LogMessageDTO stringToObject(String logMessageStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(logMessageStr, LogMessageDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Autowired
    public LogConsumer(FileRepository<Log> repository, ApplicationConfiguration configuration, DigitalSignatureServiceImpl digitalSignatureService) {
        this.repository = repository;
        this.configuration = configuration;
        this.digitalSignatureService = digitalSignatureService;
    }

}
