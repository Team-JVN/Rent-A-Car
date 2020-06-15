package jvn.RentACar.config;

import jvn.RentACar.client.PriceListClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class PriceListConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.pricelist");
        return marshaller;
    }
    @Bean
    public PriceListClient countryClient(Jaxb2Marshaller marshaller) {
        PriceListClient client = new PriceListClient();
        client.setDefaultUri("http://localhost:8080/advertisements/pricelist/ws");
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }
}
