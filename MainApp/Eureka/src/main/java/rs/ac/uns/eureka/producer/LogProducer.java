package rs.ac.uns.eureka.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.eureka.config.RabbitMQConfiguration;
import rs.ac.uns.eureka.message.Log;

@Service
public class LogProducer {

    private RabbitTemplate rabbitTemplate;

    public void send(Log log) {
        rabbitTemplate.convertAndSend(RabbitMQConfiguration.LOGS, log.toString());
    }

    @Autowired
    public LogProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
}
