package jvn.Cars.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String UPDATE_CAR_MILEAGE = "update-car-mileage";

    public static final String CAR_RATING = "car-rating";

    public static final String CAR_RATING_FOR_SEARCH_SERVICE = "car-rating-for-search-service";

    @Bean
    public Queue queueUpdateCarMileage() {
        return new Queue(UPDATE_CAR_MILEAGE, false);
    }

    public static final String LOGS = "logs";

    @Bean
    public Queue queueLogs() {
        return new Queue(LOGS, false);
    }

    @Bean
    public Queue queueCarRating() {
        return new Queue(CAR_RATING, false);
    }

    @Bean
    public Queue queueCarRatingForSearchService() {
        return new Queue(CAR_RATING_FOR_SEARCH_SERVICE, false);
    }
}
