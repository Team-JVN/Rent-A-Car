package jvn.Renting.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    private static final String CANCELED_RESERVATION = "canceled-reservation";

    private static final String REJECTED_RESERVATION = "rejected-reservation";

    private static final String ACCEPTED_RESERVATION = "accepted-reservation";

    public static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    public static final String REJECTED_COMMENT = "rejected-comment";

    public static final String CAR_RATING = "car-rating";

    public static final String MILEAGE = "mileage";

    public static final String LOGS = "logs";

    @Bean
    public Queue queueCanceledReservation() {
        return new Queue(CANCELED_RESERVATION, false);
    }

    @Bean
    public Queue queueRejectedReservation() {
        return new Queue(REJECTED_RESERVATION, false);
    }

    @Bean
    public Queue queueAcceptedReservation() {
        return new Queue(ACCEPTED_RESERVATION, false);
    }

    @Bean
    public Queue queueDeletedAdvertisement() {
        return new Queue(REJECT_ALL_REQUESTS, false);
    }

    @Bean
    public Queue queueRejectedComment() {
        return new Queue(REJECTED_COMMENT, false);
    }

    @Bean
    public Queue queueMileage() {
        return new Queue(MILEAGE, false);
    }

    @Bean
    public Queue queueCarRating() {
        return new Queue(CAR_RATING, false);
    }
}
