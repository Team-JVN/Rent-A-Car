package jvn.Renting.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.dto.message.RentReportMessageDTO;
import jvn.Renting.model.RentReport;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RentReportProducer {

    private static final String MILEAGE = "mileage";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    public void sendMileage(RentReport rentReport) {
        rabbitTemplate.convertAndSend(MILEAGE, jsonToString(new RentReportMessageDTO(rentReport.getRentInfo().getAdvertisement(), rentReport.getMadeMileage())));

    }

    private String jsonToString(RentReportMessageDTO rentReport) {
        try {
            return objectMapper.writeValueAsString(rentReport);
        } catch (JsonProcessingException e) {
            //TODO: Add to log and delete return null;
            return null;
        }
    }

    @Autowired
    public RentReportProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }
}
