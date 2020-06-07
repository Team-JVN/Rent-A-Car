package jvn.SearchService.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    public static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    public static final String EDIT_PARTIAL_ADVERTISEMENT = "advertisements-for-search-edit-partial-adv";

    public static final String EDIT_PRICE_LIST_ADVERTISEMENT = "advertisements-for-search-edit-price-list";

    @Bean
    public Queue queueCreatedAdvertisement() {
        return new Queue(ADVERTISEMENT_FOR_SEARCH, false);
    }

    @Bean
    public Queue queueDeletedAdvertisement() {
        return new Queue(DELETED_ADVERTISEMENT, false);
    }

    @Bean
    public Queue queueEditedPartialAdvertisement() {
        return new Queue(EDIT_PARTIAL_ADVERTISEMENT, false);
    }

    @Bean
    public Queue queueEditPriceListAdvertisement() {
        return new Queue(EDIT_PRICE_LIST_ADVERTISEMENT, false);
    }
}

