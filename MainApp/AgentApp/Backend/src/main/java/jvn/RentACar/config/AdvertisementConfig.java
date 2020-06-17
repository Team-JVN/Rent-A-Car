package jvn.RentACar.config;

import jvn.RentACar.client.AdvertisementClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class AdvertisementConfig {

    @Bean
    @Qualifier("marshallerAdvertisement")
    public Jaxb2Marshaller marshallerAdvertisement() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.advertisement");
        return marshaller;
    }

    @Bean
    public AdvertisementClient advertisementClient(@Qualifier("marshallerAdvertisement") Jaxb2Marshaller marshallerAdvertisement) {
        AdvertisementClient client = new AdvertisementClient();
        client.setDefaultUri("http://localhost:8080/advertisements/ws/advertisement");
        client.setMarshaller(marshallerAdvertisement);
        client.setUnmarshaller(marshallerAdvertisement);
        return client;
    }
}
