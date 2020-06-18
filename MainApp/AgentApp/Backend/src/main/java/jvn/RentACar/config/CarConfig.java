package jvn.RentACar.config;

import jvn.RentACar.client.CarClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class CarConfig {

    @Bean
    @Qualifier("marshallerCar")
    public Jaxb2Marshaller marshallerCar() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("jvn.RentACar.dto.soap.car");
        return marshaller;
    }

    @Bean
    public CarClient carClient(@Qualifier("marshallerCar") Jaxb2Marshaller marshallerCar) {
        CarClient client = new CarClient();
        client.setDefaultUri("http://localhost:8080/cars/car/ws");
        client.setMarshaller(marshallerCar);
        client.setUnmarshaller(marshallerCar);
        return client;
    }
}
