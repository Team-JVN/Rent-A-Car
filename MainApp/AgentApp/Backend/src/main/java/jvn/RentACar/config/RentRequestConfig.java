package jvn.RentACar.config;

import jvn.RentACar.client.RentRequestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class RentRequestConfig {
    @Bean
    @Qualifier("marshallerRentRequest")
    public Jaxb2Marshaller marshallerRentRequest() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.rentrequest");
        return marshaller;
    }

    @Bean
    public RentRequestClient rentRequestClient(Jaxb2Marshaller marshallerRentRequest) {
        RentRequestClient client = new RentRequestClient();
//       client.setDefaultUri("http://renting:8083/ws/rentrequest");
        // client.setDefaultUri("http://localhost:8080/renting/rentrequest/ws");
        client.setDefaultUri("http://localhost:8080/renting/ws/rentrequest");
        client.setMarshaller(marshallerRentRequest);
        client.setUnmarshaller(marshallerRentRequest);
        return client;
    }
}
