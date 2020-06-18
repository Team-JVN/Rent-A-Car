package jvn.Renting.producer;

import jvn.Renting.config.RabbitMQConfiguration;
import jvn.Renting.dto.message.Log;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
