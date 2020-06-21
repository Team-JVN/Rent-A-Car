package jvn.Cars.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Cars.dto.message.CarEditSignedDTO;
import jvn.Cars.dto.message.Log;
import jvn.Cars.dto.request.CarEditDTO;
import jvn.Cars.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarProducer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private static final String EDIT_PARTIAL_CAR = "edit-partial-car";

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    public void sendMessageForSearch(CarEditDTO carEditDTO) {
        byte[] carBytes = convertToBytes(carEditDTO);
        byte[] digitalSignature = digitalSignatureService.encrypt(carBytes);
        CarEditSignedDTO carEditSignedDTO = new CarEditSignedDTO(carBytes, digitalSignature);
        rabbitTemplate.convertAndSend(EDIT_PARTIAL_CAR, convertToString(carEditSignedDTO));
    }


    private byte[] convertToBytes(CarEditDTO obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to byte array failed", CarEditDTO.class.getSimpleName())));
            return null;
        }
    }

    private String convertToString(CarEditSignedDTO obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping %s instance to string failed", CarEditSignedDTO.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public CarProducer(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper, LogProducer logProducer,
                       DigitalSignatureService digitalSignatureService) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
