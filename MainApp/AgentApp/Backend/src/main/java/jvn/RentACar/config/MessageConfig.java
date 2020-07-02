package jvn.RentACar.config;

import jvn.RentACar.client.MessageClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class MessageConfig {
    @Bean
    @Qualifier("marshallerMessage")
    public Jaxb2Marshaller marshallerMessage() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.message");
        return marshaller;
    }

    @Bean
    public MessageClient messageClient(Jaxb2Marshaller marshallerMessage) {
        MessageClient client = new MessageClient();
        client.setDefaultUri("http://localhost:8080/renting/ws/message");
//        client.setDefaultUri("http://renting:8083/renting/ws/message");
        client.setMarshaller(marshallerMessage);
        client.setUnmarshaller(marshallerMessage);
        return client;
    }
}
