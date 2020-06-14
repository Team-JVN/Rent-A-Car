package jvn.Users.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String CANCELED_RESERVATION = "canceled-reservation";

    public static final String REJECTED_RESERVATION = "rejected-reservation";

    public static final String ACCEPTED_RESERVATION = "accepted-reservation";

    public static final String USERS_FOR_SEARCH = "users-for-search";

    public static final String REJECTED_COMMENT = "rejected-comment";

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
    public Queue queueUsersForSearch() {
        return new Queue(USERS_FOR_SEARCH, false);
    }

    @Bean
    public Queue queueRejectedComment(){ return new Queue(REJECTED_COMMENT, false); }
}
