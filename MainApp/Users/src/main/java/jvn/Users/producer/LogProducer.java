package jvn.Users.producer;

import jvn.Users.config.RabbitMQConfiguration;
import jvn.Users.dto.message.Log;
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
