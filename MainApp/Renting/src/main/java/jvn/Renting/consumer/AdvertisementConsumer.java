package jvn.Renting.consumer;

import jvn.Renting.config.RabbitMQConfiguration;
import jvn.Renting.enumeration.RentRequestStatus;
import jvn.Renting.model.RentRequest;
import jvn.Renting.producer.RentRequestProducer;
import jvn.Renting.repository.RentRequestRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementConsumer {

    private RentRequestRepository rentRequestRepository;

    private RentRequestProducer rentRequestProducer;

    @RabbitListener(queues = RabbitMQConfiguration.REJECT_ALL_REQUESTS)
    public void listen(Long advId) {
        List<RentRequest> rentRequests = rentRequestRepository.findByRentInfosAdvertisementAndRentInfosRentRequestRentRequestStatus(advId, RentRequestStatus.PENDING);
        for (RentRequest rentRequest : rentRequests) {
            rentRequest.setRentRequestStatus(RentRequestStatus.CANCELED);
            sendRejectedReservation(rentRequest.getClient(), rentRequest.getId());
        }
        rentRequestRepository.saveAll(rentRequests);
    }

    @Async
    public void sendRejectedReservation(Long clientId, Long rentRequestId) {
        rentRequestProducer.sendRejectedReservation(clientId, rentRequestId);
    }

    @Autowired
    public AdvertisementConsumer(RentRequestRepository rentRequestRepository, RentRequestProducer rentRequestProducer) {
        this.rentRequestRepository = rentRequestRepository;
        this.rentRequestProducer = rentRequestProducer;
    }
}
