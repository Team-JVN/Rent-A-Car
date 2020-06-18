package jvn.RentACar.config;

import jvn.RentACar.client.PriceListClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class PriceListConfig {
    @Bean
    @Qualifier("marshallerPriceList")
    public Jaxb2Marshaller marshallerPriceList() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.pricelist");
        return marshaller;
    }

    @Bean
    public PriceListClient priceListClient(Jaxb2Marshaller marshallerPriceList) {
        PriceListClient client = new PriceListClient();
        client.setDefaultUri("http://localhost:8080/advertisements/ws/pricelist");
        client.setMarshaller(marshallerPriceList);
        client.setUnmarshaller(marshallerPriceList);
        return client;
    }
}
