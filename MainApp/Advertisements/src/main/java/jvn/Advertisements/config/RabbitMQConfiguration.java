package jvn.Advertisements.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    private static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    private static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    private static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    @Bean
    public Queue queueCreatedAdvertisement() {
        return new Queue(ADVERTISEMENT_FOR_SEARCH, false);
    }

    @Bean
    public Queue queueRejectAllRentRequest() {
        return new Queue(REJECT_ALL_REQUESTS, false);
    }

    @Bean
    public Queue queueDeletedAdvertisement() {
        return new Queue(DELETED_ADVERTISEMENT, false);
    }
}

