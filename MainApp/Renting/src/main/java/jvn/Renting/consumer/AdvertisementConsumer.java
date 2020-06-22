package jvn.Renting.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jvn.Renting.config.RabbitMQConfiguration;
import jvn.Renting.dto.message.AdvIdSignedDTO;
import jvn.Renting.dto.message.Log;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.producer.RentRequestProducer;
import jvn.Renting.repository.RentRequestRepository;
import jvn.Renting.service.DigitalSignatureService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AdvertisementConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private final String ADVERTISEMENTS_ALIAS = "advertisements";

    private ObjectMapper objectMapper;

    private RentRequestRepository rentRequestRepository;

    private RentRequestProducer rentRequestProducer;

    private LogProducer logProducer;

    private DigitalSignatureService digitalSignatureService;

    @RabbitListener(queues = RabbitMQConfiguration.REJECT_ALL_REQUESTS)
    public void listen(String advIdStr) {
        AdvIdSignedDTO advIdSignedDTO = stringToAdvIdSignedDTO(advIdStr);
        if (digitalSignatureService.decrypt(ADVERTISEMENTS_ALIAS, advIdSignedDTO.getAdvIdBytes(), advIdSignedDTO.getDigitalSignature())) {
            Long advId = bytesToLong(advIdSignedDTO.getAdvIdBytes());
            List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatus(advId, RentRequestStatus.PENDING);
            StringBuilder sb = new StringBuilder();
            for (RentRequest rentRequest : rentRequests) {
                rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
                sendRejectedReservation(rentRequest.getClient(), rentRequest.getId());
                sb.append(rentRequest.getId());
                sb.append(", ");
            }
            rentRequestRepository.saveAll(rentRequests);
            logProducer.send(new Log(Log.INFO, Log.getServiceName(CLASS_PATH), CLASS_NAME, "SRQ", String.format("Rent requests %s are REJECTED, because advertisement %s was deleted", sb.toString(), advId)));
        }
    }

    @Async
    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        rentRequestProducer.sendRejectedReservation(clientId, rentRequestId);
    }


    private AdvIdSignedDTO stringToAdvIdSignedDTO(String advIdStr) {
        try {
            return objectMapper.readValue(advIdStr, AdvIdSignedDTO.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private Long bytesToLong(byte[] byteArray) {
        try {
            return objectMapper.readValue(byteArray, Long.class);
        } catch (IOException e) {
            logProducer.send(new Log(Log.ERROR, Log.getServiceName(CLASS_PATH), CLASS_NAME, "OMP", String.format("Mapping byte array to %s failed", Long.class.getSimpleName())));
            return null;
        }
    }

    @Autowired
    public AdvertisementConsumer(ObjectMapper objectMapper, RentRequestRepository rentRequestRepository,
                                 RentRequestProducer rentRequestProducer, LogProducer logProducer,
                                 DigitalSignatureService digitalSignatureService) {
        this.objectMapper = objectMapper;
        this.rentRequestRepository = rentRequestRepository;
        this.rentRequestProducer = rentRequestProducer;
        this.logProducer = logProducer;
        this.digitalSignatureService = digitalSignatureService;
    }
}
