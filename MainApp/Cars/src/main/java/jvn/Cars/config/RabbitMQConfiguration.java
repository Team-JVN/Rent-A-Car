package jvn.Cars.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String LOGS = "logs";

    @Bean
    public Queue queueLogs() {
        return new Queue(LOGS, false);
    }
}
