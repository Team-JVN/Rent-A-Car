package jvn.Advertisements.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    private static final String ADVERTISEMENT_FOR_SEARCH = "advertisements-for-search";

    private static final String REJECT_ALL_REQUESTS = "reject-all-requests";

    private static final String DELETED_ADVERTISEMENT = "advertisements-for-search-deleted-adv";

    private static final String EDIT_PARTIAL_ADVERTISEMENT = "advertisements-for-search-edit-partial-adv";

    private static final String EDIT_PRICE_LIST_ADVERTISEMENT = "advertisements-for-search-edit-price-list";

    public static final String MILEAGE = "mileage";

    public static final String UPDATE_CAR_MILEAGE = "update-car-mileage";

    public static final String LOGS = "logs";

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

    @Bean
    public Queue queueEditedPartialAdvertisement() {
        return new Queue(EDIT_PARTIAL_ADVERTISEMENT, false);
    }

    @Bean
    public Queue queueEditPriceListAdvertisement() {
        return new Queue(EDIT_PRICE_LIST_ADVERTISEMENT, false);
    }

    @Bean
    public Queue queueMileage(){ return new Queue(MILEAGE, false); }

   @Bean
   public Queue queueUpdateCarMileage(){ return new Queue(UPDATE_CAR_MILEAGE, false); }

    public Queue queueLogs() {
        return new Queue(LOGS, false);
    }
}

