package jvn.Renting.consumer;

import jvn.Renting.config.RabbitMQConfiguration;
import jvn.Renting.dto.message.Log;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.LogProducer;
import jvn.Renting.producer.RentRequestProducer;
import jvn.Renting.repository.RentRequestRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementConsumer {

    private final String CLASS_PATH = this.getClass().getCanonicalName();
    private final String CLASS_NAME = this.getClass().getSimpleName();

    private RentRequestRepository rentRequestRepository;

    private RentRequestProducer rentRequestProducer;

    private LogProducer logProducer;

    @RabbitListener(queues = RabbitMQConfiguration.REJECT_ALL_REQUESTS)
    public void listen(Long advId) {
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

    @Async
    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        rentRequestProducer.sendRejectedReservation(clientId, rentRequestId);
    }

    @Autowired
    public AdvertisementConsumer(RentRequestRepository rentRequestRepository, RentRequestProducer rentRequestProducer,
                                 LogProducer logProducer) {
        this.rentRequestRepository = rentRequestRepository;
        this.rentRequestProducer = rentRequestProducer;
        this.logProducer = logProducer;
    }
}
