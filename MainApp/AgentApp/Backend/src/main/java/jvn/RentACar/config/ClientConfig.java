package jvn.RentACar.config;

import jvn.RentACar.client.ClientClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class ClientConfig {
    @Bean
    @Qualifier("marshallerClient")
    public Jaxb2Marshaller marshallerClient() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.client");
        return marshaller;
    }

    @Bean
    public ClientClient clientClient(Jaxb2Marshaller marshallerClient) {
        ClientClient client = new ClientClient();
        client.setDefaultUri("http://localhost:8080/users/ws");
        client.setMarshaller(marshallerClient);
        client.setUnmarshaller(marshallerClient);
        return client;
    }
}
